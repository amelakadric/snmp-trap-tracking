package src_package;

import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.time.LocalDateTime;

import javax.swing.JFrame;

import com.ireasoning.protocol.*;
import com.ireasoning.protocol.snmp.MibUtil;
import com.ireasoning.protocol.snmp.SnmpConst;
import com.ireasoning.protocol.snmp.SnmpDataType;
import com.ireasoning.protocol.snmp.SnmpTrapdSession;
import com.ireasoning.protocol.snmp.SnmpV2Notification;

public class TrapListener extends Frame implements Listener 
{
	private TextArea textArea;
	SnmpTrapdSession session;
	
	
	public TrapListener() {
		
    	setLayout(new GridLayout());
    	setTitle("RM2 projekat, varijanta 6");
		setVisible(true);
    	setBounds(700, 200, 500, 500);
		textArea = new TextArea();
		textArea.setEditable(false);
		add(textArea);
//		pack();
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
	            session.close();
				dispose();
			}
		});
		
    }

	
    public static void main(String[] args)
    {
    	TrapListener tl = new TrapListener();
    	
    
        tl.trapd();         
//		tl.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
             
    }
    

    void trapd()
    {
    	
        try
        {
        	session = new SnmpTrapdSession(1620);
            session.addListener(this);
            textArea.setText(textArea.getText()+ "Pokrenuta sesija na portu 1620 \n\n");
           
            session.waitForTrap();
        }
        catch(IOException e)
        {
            System.out.println(e);
            e.printStackTrace();
        }
        
    }

    public void handleMsg(Object msgSender, Msg msg)  
    {
        if(msg.getType() == Msg.ERROR_TYPE)
        {
            ErrorMsg err = (ErrorMsg) msg;
            System.out.println( "Error occurred. Error code: " + err.getErrorCode() + ". Error message: " + err.getErrorString() );
        }
        else
        {
            printTrap((SnmpDataType)msg);
        }
    }
    
    private void printTrap(SnmpDataType t)
    {
        if(t.getType() == SnmpConst.V2TRAP)
        {
            SnmpV2Notification trap = (SnmpV2Notification)t;
     
            String name = MibUtil.translateOID("" + trap.getSnmpTrapOID(), false);
            if(name == null)
            {
                name = "" + trap.getSnmpTrapOID();
            }
            if (name.equals(".1.3.6.1.2.1.15.7.1") || name.equals(".1.3.6.1.2.1.15.7.2")) {
            	String ipRutera=trap.getSourceAddress();
            	String ruter="";
            	if(ipRutera.equals("192.168.122.100")) {
            		ruter= "R1";
            	}
            	else if(ipRutera.equals("192.168.12.2")) {
            		ruter= "R2";
            	}
            	else if(ipRutera.equals("192.168.13.3")) {
            		ruter= "R3";
            	}
            	String str = "Trap na ruteru ";
            	str=str+ruter+"\nIP="+ipRutera +"\n";
//            	str=str+"Time stamp: " + trap.getSysUpTimeString()+ "\n"+ "\n";
            	LocalDateTime ldt = LocalDateTime.now();
            	str=str+"Time stamp: " + ldt.toString() + "\n"+ "\n";
            	textArea.setText(textArea.getText()+ str);
//            	pack();            
            	}
        }
      
    }
    
    
};

//komande
//snmp-server enable traps bgp
//snmp-server host 192.168.122.1 traps version 2c si2019 udp-port 1620 bgp

    