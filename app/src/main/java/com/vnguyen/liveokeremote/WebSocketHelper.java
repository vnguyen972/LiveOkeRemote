package com.vnguyen.liveokeremote;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketHelper {
    private WebSocketClient mWebSocketClient;
    private MainActivity context;
    private URI uri;

    public WebSocketHelper(Context context) {
        this.context = (MainActivity) context;
        if (this.context.ipAddress == null || this.context.ipAddress.equals("")) {
            this.context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    (new AlertDialogHelper(WebSocketHelper.this.context)).
                            popupIPAddressDialogGeneric();
                }
            });
        }
    }

    public void init(URI uri) {
        uri = URI.create("ws://" + this.context.ipAddress + ":8181");
        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.i(context.app.TAG, "Websocket: Opened");
            }

            @Override
            public void onMessage(String message) {
                Log.i(context.app.TAG,"Websocket: Message - " + message);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.i(context.app.TAG,"Websocket: Closed " + reason);
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SwitchCompat onOffSwitch = (SwitchCompat) context.onOffSwitch.getActionView().findViewById(R.id.switchForActionBar);
                        if (onOffSwitch.isChecked()) {
                            onOffSwitch.toggle();
                        }
                    }
                });
            }

            @Override
            public void onError(Exception ex) {
                final String errMsg = ex.getMessage();
                Log.i(context.app.TAG,"Websocket: Error " + ex.getMessage());
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SnackbarManager.show(Snackbar.with(context)
                                .type(SnackbarType.MULTI_LINE)
                                .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                                .textColor(Color.WHITE)
                                .color(Color.RED)
                                .text("ERROR: " + errMsg));
                    }
                });
            }
        };
    }

    public void connect() {
        init(uri);
        mWebSocketClient.connect();
    }

    public void disconnect() {
         mWebSocketClient.close();
    }

    public void sendMessage(String message) {
        mWebSocketClient.send(message);
    }
}
