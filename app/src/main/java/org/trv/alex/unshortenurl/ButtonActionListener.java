package org.trv.alex.unshortenurl;

import android.os.Bundle;

public interface ButtonActionListener {

    void onPositive(Bundle args, DialogType type);

    void onNegative(Bundle args, DialogType type);

    void onNeutral(Bundle args, DialogType type);

}
