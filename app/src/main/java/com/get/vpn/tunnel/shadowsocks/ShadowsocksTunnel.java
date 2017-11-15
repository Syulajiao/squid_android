package com.get.vpn.tunnel.shadowsocks;

import android.util.Log;

import com.get.vpn.tunnel.Tunnel;
import com.get.vpn.utils.str2Hex;

import java.nio.ByteBuffer;
import java.nio.channels.Selector;

public class ShadowsocksTunnel extends Tunnel {

	private ICrypt m_Encryptor;
	private ShadowsocksConfig m_Config;
	private boolean m_TunnelEstablished;

	private String m_strPrefixHeader;

	public ShadowsocksTunnel(ShadowsocksConfig config, String strPrefix, Selector selector) throws Exception {
		super(config.ServerAddress,selector);
		m_Config=config;
		m_strPrefixHeader = strPrefix;
        m_Encryptor = CryptFactory.get(m_Config.EncryptMethod, m_Config.Password);

	}
	/*
	@Override
	protected void onConnected(ByteBuffer buffer) throws Exception {

        buffer.clear();
        // https://shadowsocks.org/en/spec/protocol.html

        buffer.put((byte)0x03);//domain
        byte[] domainBytes=m_DestAddress.getHostName().getBytes();
        buffer.put((byte)domainBytes.length);//domain length;
        buffer.put(domainBytes);
        buffer.putShort((short)m_DestAddress.getPort());
        buffer.flip();
        byte[] _header=new byte[buffer.limit()];
        buffer.get(_header);

        buffer.clear();
        buffer.put(m_Encryptor.encrypt(_header));
        buffer.flip();

        if(write(buffer, true)){
        	m_TunnelEstablished=true;
        	onTunnelEstablished();
        }else {
        	m_TunnelEstablished=true;
			this.beginReceive();
		}
	}
	*/
	@Override
	protected void onConnected(ByteBuffer buffer) throws Exception {

        buffer.clear();

		// 0x06 length(2Byte)email||uuid||os||os-version||device-model||app-version 0x03 playload(shadowsocks)
		// m_strPrefixHeader格式为email||uuid||os||os-version||device-model||app-version
		if (!m_strPrefixHeader.isEmpty()) {
            buffer.put((byte)0x06);
            byte[] userInfo = m_strPrefixHeader.getBytes();
            buffer.putShort((short)userInfo.length);
            buffer.put(userInfo);
		}

        buffer.put((byte)0x03);//domain
        byte[] domainBytes=m_DestAddress.getHostName().getBytes();
        buffer.put((byte)domainBytes.length);//domain length;
        buffer.put(domainBytes);
        buffer.putShort((short)m_DestAddress.getPort());
        buffer.flip();
        byte[] _header=new byte[buffer.limit()];
        buffer.get(_header);

        buffer.clear();
        buffer.put(m_Encryptor.encrypt(_header));
        buffer.flip();

        if(write(buffer, true)){
        	m_TunnelEstablished=true;
        	onTunnelEstablished();
        }else {
        	m_TunnelEstablished=true;
			this.beginReceive();
		}
	}

	@Override
	protected boolean isTunnelEstablished() {
		return m_TunnelEstablished;
	}

	@Override
	protected void beforeSend(ByteBuffer buffer) throws Exception {

        byte[] bytes = new byte[buffer.limit()];
        buffer.get(bytes);

		byte[] newbytes = m_Encryptor.encrypt(bytes);

        buffer.clear();
        buffer.put(newbytes);
        buffer.flip();
	}

	@Override
	protected void afterReceived(ByteBuffer buffer) throws Exception {
        byte[] bytes = new byte[buffer.limit()];
        buffer.get(bytes);
        byte[] newbytes = m_Encryptor.decrypt(bytes);
        String s=new String(newbytes);
        buffer.clear();
        buffer.put(newbytes);
        buffer.flip();
	}

	@Override
	protected void onDispose() {
		 m_Config=null;
		 m_Encryptor=null;
	}

}
