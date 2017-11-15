package com.get.vpn.ServerTester;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.get.vpn.model.VpnModel;
import com.get.vpn.tunnel.shadowsocks.CryptFactory;
import com.get.vpn.tunnel.shadowsocks.ICrypt;
import com.get.vpn.utils.str2Hex;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;

/**
 * Created by istmedia-m1 on 6/21/17.
 */

public class ServerTester extends HandlerThread {
    private static String TAG = "ServerTester";

    public static final  int MSG_NOTI_CONNECTTIME = 201;

    private static final int MSG_TEST_CONNECTION = 101;

    private Handler     mHandlerRev = null;
    private Handler     mThisHandler = null;
    private VpnModel    mTestServer = null;

    public void onLooperPrepared() {
        mThisHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_TEST_CONNECTION:
                        _doMsgTestConnection();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    public ServerTester(Handler revHandler) {
        super(TAG);
        mHandlerRev = revHandler;
    }

    public void testConnection(VpnModel server) {
        if (null != mThisHandler && null != server) {
            mTestServer = server;
            mThisHandler.obtainMessage(MSG_TEST_CONNECTION)
                    .sendToTarget();
        }
    }
    /*
    nConnectTime == -1, failed to connect
     */
    private void _notifyConnectRet(long nConnectTime) {
        if (null != mHandlerRev) {
            mHandlerRev.obtainMessage(MSG_NOTI_CONNECTTIME, nConnectTime)
                    .sendToTarget();
        }
    }

    private void _doMsgTestConnection() {
        try {

            long startTime = System.nanoTime();

            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(mTestServer.getIp(), mTestServer.getPort()), 5000);

            //connect 速度
            long estimatedTime = System.nanoTime()-startTime;

            // 发测试info
            OutputStream outS = socket.getOutputStream();
            byte[] bTestInfo = _makeTestInfo();
            outS.write(bTestInfo);
            outS.flush();
//          socket.shutdownOutput(); // 不在发数据 read 非阻塞


            //收数据
            socket.setSoTimeout(5000); //5s 后就不收数据
            InputStream input = socket.getInputStream();

            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            int nr = 0;
            byte[] sw = new byte[64];
            while ((nr=input.read(sw, 0, 64)) != -1) {
                byteStream.write(sw, 0, nr);
                break;
            }
            /*
            if (nr == 64) {
                ICrypt mCrypt = CryptFactory.get(mTestServer.getMethod(), mTestServer.getPassword());
                byte[] ret = mCrypt.decrypt(byteStream.toByteArray());
                String strTmp = new String(ret);
                String str = new String(ret).substring(0, 4);

                if (str.equals("HTTP")) {
                    _notifyConnectRet(estimatedTime);
                } else {
                    _notifyConnectRet(-2);
                }
                */
            // 依赖服务器的数据返回（iv）
            if (nr>=8) {
                _notifyConnectRet(estimatedTime);
            }else {
                _notifyConnectRet(-2);
            }

            outS.close();
            socket.close();

        } catch (SocketTimeoutException aa) {
            _notifyConnectRet(-1);
        } catch (IOException e) {
            _notifyConnectRet(-1);
        };
    }

    private byte[] _makeTestInfo() {
        if (null == mTestServer) {
            return null;
        }

        // 按照最新的协议探测
        // 0x06 length(2Byte)email||uuid||os||os-version||device-model||app-version 0x03 playload(shadowsocks)
		/*String strTmp = "android_detector_email||";
		strTmp += "android_detector_uuid||";
		strTmp += "android||";
		strTmp += "7.1.1||";
		strTmp += "google||";
		strTmp += "1.0.0";
		*/
        String strTmp = "oid_detector_email||";
		strTmp += "android_detector_uuid||";
		strTmp += "android||";
		strTmp += "7.1.1||";
		strTmp += "google||";
		strTmp += "1.0.0";

        ByteBuffer buffer = ByteBuffer.allocate(256);
		buffer.put((byte)0x06);
		byte[] userInfo = strTmp.getBytes();
		buffer.putShort((short)userInfo.length);
		buffer.put(userInfo);
		buffer.flip();
		byte[] bPre = new byte[buffer.limit()];
		buffer.get(bPre);

        String t = str2Hex.bytes2HexString(bPre);
        Log.i(TAG, t);

        //String strHex = "06004C616E64726F69645F6465746563746F725F656D61696C7C7C616E64726F69645F6465746563746F725F757569647C7C616E64726F69647C7C372E312E317C7C676F6F676C657C7C312E302E30";
        String strHex = "030e7777772e676f6f676c652e636f6d0050474554202f20485454502f312e310d0a486f73743a207777772e676f6f676c652e636f6d0d0a557365722d4167656e743a206375726c2f372e34332e300d0a4163636570743a202a2f2a0d0a0d0a";
        byte[] bHex = str2Hex.hexStr2Bytes(strHex);

        byte[] bSend = str2Hex.addBytes(bPre, bHex);

        ICrypt mCrypt = CryptFactory.get(mTestServer.getMethod(), mTestServer.getPassword());

        if (null == mCrypt) {
            return null;
        }

        return  mCrypt.encrypt(bSend);
    }

}
