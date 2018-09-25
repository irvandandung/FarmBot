package com.ghootenk.user.farmbot.sync;

import org.json.JSONObject;

public interface AsyncResult  {
    void onResult(JSONObject object);
}
