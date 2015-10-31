package com.ftloverdrive.packer;


import java.awt.*;

public class OverdrivePacker {

	public static void main( String args[] ) {
		EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                PackerFrame pf = new PackerFrame();
                pf.setVisible(true);
            }
        });
	}
}
