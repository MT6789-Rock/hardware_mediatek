/*
 * Copyright (c) 2023 The LineageOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qualcomm.qti.poweroffalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.lineageos.poweroffalarm.PowerOffAlarm;

public class PowerOffAlarmBroadcastReceiver extends BroadcastReceiver {
    private static final String ACTION_CANCEL_POWEROFF_ALARM = "org.codeaurora.poweroffalarm.action.CANCEL_ALARM";
    private static final String ACTION_SET_POWEROFF_ALARM = "org.codeaurora.poweroffalarm.action.SET_ALARM";
    private static final String TAG = "PowerOffAlarmBroadcastReceiver";
    private static final String TIME = "time";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            Log.w(TAG, "Dropping null intent.");
            return;
        }
        String action = intent.getAction();
        boolean isSetAlarm = ACTION_SET_POWEROFF_ALARM.equals(action);
        boolean isCancelAlarm = ACTION_CANCEL_POWEROFF_ALARM.equals(action);
        final long alarmTime = intent.getLongExtra(TIME, -1L);
        if (!(isSetAlarm || isCancelAlarm) || alarmTime <= 0) {
            Log.w(TAG, "Dropping malformed intent with action " + action + ". "
                    + "alarmTime=" + alarmTime);
            return;
        }
        Log.d(TAG, "Receive " + (isSetAlarm ? "set" : "cancel") + " intent.");
        final PowerOffAlarm powerOffAlarm = new PowerOffAlarm(context);
        final long alarmInPref = powerOffAlarm.getAlarmFromPreference();
        Log.d(TAG, "alarmTime=" + alarmTime + " alarmInPref=" + alarmInPref);
        if (isSetAlarm) {
            powerOffAlarm.saveAlarmToPreference(alarmTime);
            powerOffAlarm.setAlarmToRtc(alarmTime);
        } else if (isCancelAlarm && alarmTime == alarmInPref) {
            powerOffAlarm.saveAlarmToPreference(0L);
            powerOffAlarm.cancelAlarmInRtc();
        }
    }
}