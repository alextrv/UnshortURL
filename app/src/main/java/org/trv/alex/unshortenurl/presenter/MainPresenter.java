package org.trv.alex.unshortenurl.presenter;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.SparseArray;
import android.view.View;

import org.trv.alex.unshortenurl.R;
import org.trv.alex.unshortenurl.model.HistoryModel;
import org.trv.alex.unshortenurl.model.HistoryModelContract;
import org.trv.alex.unshortenurl.model.HistoryURL;
import org.trv.alex.unshortenurl.ui.view.MainViewContract;
import org.trv.alex.unshortenurl.ui.view.activity.MainActivity;
import org.trv.alex.unshortenurl.ui.view.fragment.HistoryFragment;
import org.trv.alex.unshortenurl.ui.view.fragment.MainFragment;
import org.trv.alex.unshortenurl.util.Network;
import org.trv.alex.unshortenurl.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainPresenter implements MainPresenterContract {

    public static final MainPresenterContract INSTANCE = new MainPresenter();

    public static final int APPEND_ITEM_SIZE = 20;

    private static final MainViewContract DEFAULT_STUB_VIEW = new StubView();

    private MainActivity mMainActivity;
    private HistoryModelContract mHistoryModelContract;
    private final SparseArray<MainViewContract> mViews = new SparseArray<>(2);

    private List<HistoryURL> mUndoItems = Collections.emptyList();

    private int mHistoryListSize;

    // Network thread
    private HandlerThread mNetworkThread;
    private Handler mNetworkHandler;

    // DB thread
    private HandlerThread mDBThread;
    private Handler mDBHandler;

    // Main thread
    private Handler mMainHandler = new Handler(Looper.getMainLooper());

    private MainPresenter() {
        initThreads();
    }

    private void initThreads() {
        mNetworkThread = new HandlerThread("NetworkThread");
        mNetworkThread.start();
        mNetworkHandler = new Handler(mNetworkThread.getLooper());

        mDBThread = new HandlerThread("DBThread");
        mDBThread.start();
        mDBHandler = new Handler(mDBThread.getLooper());
    }

    @Override
    public void resolveURL(CharSequence url, boolean deep) {
        mMainHandler.post(() -> view(MainFragment.TAB_POSITION).showProgress());
        mNetworkHandler.post(() -> {
            final int SUCCESS_CODE = 0;
            List<HistoryURL> result;
            int messageId;
            if (Network.isOnline()) {
                List<HistoryURL> list = mHistoryModelContract.getLongURL(String.valueOf(url), deep);
                if (list == null) {
                    messageId = R.string.error_occurred;
                } else if (list.isEmpty()) {
                    messageId = R.string.not_short_url_string;
                } else {
                    messageId = SUCCESS_CODE;
                }
                result = Utils.unmodifiableList(list);
            } else {
                messageId = R.string.no_inet_connection_string;
                result = Collections.emptyList();
            }
            mMainHandler.post(() -> {
                view(MainFragment.TAB_POSITION).updateListUI(result, -1);
                view(MainFragment.TAB_POSITION).dismissProgress();
                if (messageId == SUCCESS_CODE) {
                    mHistoryListSize++;
                    getHistory(false);
                } else {
                    view(MainFragment.TAB_POSITION).showMessage(messageId);
                }
            });
        });
    }

    @Override
    public void attachActivity(MainActivity activity) {
        mMainActivity = activity;
        mHistoryModelContract = HistoryModel.get(activity);
    }

    @Override
    public void detachActivity() {
        mMainActivity = null;
    }

    @Override
    public void addView(int position, MainViewContract view) {
        mViews.append(position, view);
    }

    @Override
    public void removeView(int position) {
        mViews.delete(position);
    }

    @Override
    public void getHistory(boolean loadMore) {
        mDBHandler.post(() -> {
            int position;
            List<HistoryURL> items;
            if (loadMore) {
                position = mHistoryListSize;
                items = mHistoryModelContract.getItems(position, APPEND_ITEM_SIZE);
                mHistoryListSize += items.size();
            } else {
                position = -1;
                if (mHistoryListSize == 0) {
                    mHistoryListSize = APPEND_ITEM_SIZE;
                }
                items = mHistoryModelContract.getItems(0, mHistoryListSize);
                mHistoryListSize = items.size();
            }
            mMainHandler.post(() -> {
                view(HistoryFragment.TAB_POSITION).updateListUI(items, position);
            });
        });
    }

    @Override
    public void clearAllHistory() {
        mMainHandler.post(() -> view(HistoryFragment.TAB_POSITION).showProgress());
        mDBHandler.post(() -> {
            mHistoryModelContract.clearAllItems();
            mHistoryListSize = 0;
            mMainHandler.post(() -> {
                view(HistoryFragment.TAB_POSITION).updateListUI(Collections.emptyList(), -1);
                view(HistoryFragment.TAB_POSITION).dismissProgress();
            });
        });
    }

    @Override
    public void getAllInheritors(long id, ResultListener listener) {
        mDBHandler.post(() -> {
            List<HistoryURL> result = mHistoryModelContract.getAllInheritors(id);
            mMainHandler.post(() -> listener.onResultReceived(result));
        });
    }

    @Override
    public void deleteItem(HistoryURL historyURL, final int viewPosition) {
        mDBHandler.post(() -> {
            mUndoItems = new ArrayList<>();
            mUndoItems.add(historyURL);
            long parentId = historyURL.getId();
            mHistoryModelContract.deleteItem(historyURL.getId());
            HistoryURL h;
            while ((h = mHistoryModelContract.getItemByParentId(parentId)) != null) {
                mUndoItems.add(h);
                mHistoryModelContract.deleteItem(h.getId());
                parentId = h.getId();
            }
            mHistoryListSize--;
            mMainHandler.post(() -> {
                view(HistoryFragment.TAB_POSITION).deleteListItem(viewPosition);
                if (mHistoryListSize == 0) {
                    view(HistoryFragment.TAB_POSITION).updateListUI(Collections.emptyList(), -1);
                }
                view(HistoryFragment.TAB_POSITION)
                        .showMessageWithAction(R.string.item_deleted_string, v -> {
                            this.undoDeleteItem();
                            mHistoryListSize++;
                            this.getHistory(false);
                        });
            });
        });
    }

    @Override
    public void undoDeleteItem() {
        mDBHandler.post(() -> {
            if (!mUndoItems.isEmpty()) {
                mHistoryModelContract.addItems(mUndoItems);
            }
        });
    }

    @Override
    public void urlSelected(String url) {
        mMainActivity.showInfoDialog(url);
    }

    @Override
    public void copyUrl(String url) {
        Utils.setClipboardText(mMainActivity, url);
        view(mMainActivity.getViewPager().getCurrentItem()).showMessage(R.string.url_copied_string);
    }

    @Override
    public void shareUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, url);
        intent.setType("text/plain");
        mMainActivity.startActivity(intent);
    }

    @Override
    public void openUrl(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        mMainActivity.startActivity(browserIntent);
    }

    @Override
    public void newIntent(Intent intent) {
        if (Intent.ACTION_MAIN.equals(intent.getAction())) {
            return;
        }
        String url = Utils.getURLFromIntent(intent);
        view(MainFragment.TAB_POSITION).setNewURL(url);
        view(MainFragment.TAB_POSITION).updateListUI(Collections.emptyList(), -1);
        mMainActivity.getViewPager().setCurrentItem(MainFragment.TAB_POSITION, true);
    }

    private MainViewContract view(int position) {
        return mViews.get(position, DEFAULT_STUB_VIEW);
    }

    /**
     * Mock class that implements MainViewContract interface and does nothing.
     * Used as a default value in order not to get NPE.
     */
    final static class StubView implements MainViewContract {
        @Override
        public void updateListUI(List<HistoryURL> list, int insertPosition) {
        }

        @Override
        public void deleteListItem(int position) {
        }

        @Override
        public void showProgress() {
        }

        @Override
        public void dismissProgress() {
        }

        @Override
        public void showMessage(int messageId) {
        }

        @Override
        public void showMessageWithAction(int messageId, View.OnClickListener listener) {
        }

        @Override
        public void setNewURL(String url) {
        }
    }

}
