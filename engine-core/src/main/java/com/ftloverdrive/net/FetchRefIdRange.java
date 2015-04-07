package com.ftloverdrive.net;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface FetchRefIdRange extends Remote {

	public Range fetchRefIdRange() throws RemoteException;
}
