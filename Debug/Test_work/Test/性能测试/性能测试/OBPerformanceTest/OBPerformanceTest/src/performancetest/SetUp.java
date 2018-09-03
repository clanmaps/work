package performancetest;

public class SetUp {

	public static void main(String[] args) throws InterruptedException {
		if(args[0].equals("0")) {
			killOB();
			deleData();
			setUp();
			setmMster();
			bootstrap();
		} else if(args[0].equals("1")) {
			killOB();
			setUp();
			setmMster();
		} else if(args[0].equals("2")) {
			killOB();
		} else if(args[0].equals("00")) {
			killOB();
			deleData();
			setUp1();
			setmMster();
			bootstrap();
		} else if(args[0].equals("3")) {
			updateOomAdj();
		}
	}

	private static void killOB() {
		for(int i = 0; i <= 9; i++) {
			RemoteShell.exec("10.11.1.19" + i, "liyuming", "304624lym", 
			"sh /home/liyuming/scalablecommit/killob.sh");
		}
		System.out.println("kill OB success!");
	}

	private static void updateOomAdj() {
		for(int i = 0; i <= 9; i++) {
			RemoteShell.exec("10.11.1.19" + i, "root", "aaaaa", 
			"sh /home/liyuming/scalablecommit/update_oom_adj.sh\n");
		}
		System.out.println("update oom_adj success!");
	}

	private static void deleData() {
		for(int i = 0; i <= 9; i++) {
			RemoteShell.exec("10.11.1.19" + i, "liyuming", "304624lym", 
					"rm -r /home/liyuming/scalablecommit/oceanbase/log\n " +
					"rm /home/liyuming/scalablecommit/oceanbase/etc/*config.bin\n " +
			"sh /home/liyuming/scalablecommit/allinit.sh");
		}
		System.out.println("delete data success!");
	}

	private static void setUp() throws InterruptedException {
		RemoteShell.exec("10.11.1.193", "liyuming", "304624lym", 
		"cd /home/liyuming/scalablecommit/oceanbase\n sh upssetup.sh 193 eth0 3");
		Thread.sleep(1200);
		RemoteShell.exec("10.11.1.192", "liyuming", "304624lym", 
		"cd /home/liyuming/scalablecommit/oceanbase\n sh cssetup.sh 193 eth1");
		Thread.sleep(1200);
		RemoteShell.exec("10.11.1.194", "liyuming", "304624lym", 
		"cd /home/liyuming/scalablecommit/oceanbase\n sh cssetup.sh 193 bond0");
		Thread.sleep(1200);
		RemoteShell.exec("10.11.1.195", "liyuming", "304624lym", 
		"cd /home/liyuming/scalablecommit/oceanbase\n sh cssetup.sh 193 bond0");
		Thread.sleep(1200);
		RemoteShell.exec("10.11.1.196", "liyuming", "304624lym", 
		"cd /home/liyuming/scalablecommit/oceanbase\n sh cssetup.sh 193 bond0");
		Thread.sleep(1200);
		RemoteShell.exec("10.11.1.197", "liyuming", "304624lym", 
		"cd /home/liyuming/scalablecommit/oceanbase\n sh cssetup.sh 193 eth0");
		Thread.sleep(1200);
		RemoteShell.exec("10.11.1.198", "liyuming", "304624lym", 
		"cd /home/liyuming/scalablecommit/oceanbase\n sh cssetup.sh 193 eth0");
		Thread.sleep(1200);
		RemoteShell.exec("10.11.1.199", "liyuming", "304624lym", 
		"cd /home/liyuming/scalablecommit/oceanbase\n sh cssetup.sh 193 eth0");

		Thread.sleep(1200);
		RemoteShell.exec("10.11.1.190", "liyuming", "304624lym", 
		"cd /home/liyuming/scalablecommit/oceanbase\n sh upssetup.sh 190 eth0 1");
		Thread.sleep(1200);
		RemoteShell.exec("10.11.1.190", "liyuming", "304624lym", 
		"cd /home/liyuming/scalablecommit/oceanbase\n sh cssetup.sh 190 eth0");

		Thread.sleep(1200);
		RemoteShell.exec("10.11.1.191", "liyuming", "304624lym", 
		"cd /home/liyuming/scalablecommit/oceanbase\n sh upssetup.sh 191 bond0 2");
		Thread.sleep(1200);
		RemoteShell.exec("10.11.1.191", "liyuming", "304624lym", 
		"cd /home/liyuming/scalablecommit/oceanbase\n sh cssetup.sh 191 bond0");
		System.out.println("set up servers success!");
	}

	private static void setUp1() throws InterruptedException {
		RemoteShell.exec("10.11.1.193", "liyuming", "304624lym", 
		"cd /home/liyuming/scalablecommit/oceanbase\n sh upssetup1.sh 193 eth0 3");
		Thread.sleep(1200);
		RemoteShell.exec("10.11.1.190", "liyuming", "304624lym", 
		"cd /home/liyuming/scalablecommit/oceanbase\n sh cssetup.sh 193 eth0");
		Thread.sleep(1200);
		RemoteShell.exec("10.11.1.191", "liyuming", "304624lym", 
		"cd /home/liyuming/scalablecommit/oceanbase\n sh cssetup.sh 193 bond0");
		System.out.println("set up servers success!");
		Thread.sleep(1200);
		RemoteShell.exec("10.11.1.192", "liyuming", "304624lym", 
		"cd /home/liyuming/scalablecommit/oceanbase\n sh cssetup.sh 193 eth1");
		Thread.sleep(1200);
		RemoteShell.exec("10.11.1.194", "liyuming", "304624lym", 
		"cd /home/liyuming/scalablecommit/oceanbase\n sh cssetup.sh 193 bond0");
		Thread.sleep(1200);
		RemoteShell.exec("10.11.1.195", "liyuming", "304624lym", 
		"cd /home/liyuming/scalablecommit/oceanbase\n sh cssetup.sh 193 bond0");
		Thread.sleep(1200);
		RemoteShell.exec("10.11.1.196", "liyuming", "304624lym", 
		"cd /home/liyuming/scalablecommit/oceanbase\n sh cssetup.sh 193 bond0");
		Thread.sleep(1200);
		RemoteShell.exec("10.11.1.197", "liyuming", "304624lym", 
		"cd /home/liyuming/scalablecommit/oceanbase\n sh cssetup.sh 193 eth0");
		Thread.sleep(1200);
		RemoteShell.exec("10.11.1.198", "liyuming", "304624lym", 
		"cd /home/liyuming/scalablecommit/oceanbase\n sh cssetup.sh 193 eth0");
		Thread.sleep(1200);
		RemoteShell.exec("10.11.1.199", "liyuming", "304624lym", 
		"cd /home/liyuming/scalablecommit/oceanbase\n sh cssetup.sh 193 eth0");
	}

	private static void setmMster() throws InterruptedException {
		Thread.sleep(20000);
		String res = RemoteShell.exec("10.11.1.193", "liyuming", "304624lym", 
		"cd /home/liyuming/scalablecommit/oceanbase\n sh setmaster.sh");
		System.out.print(res);
	}

	private static void bootstrap() throws InterruptedException {
		Thread.sleep(30000);
		String res = RemoteShell.exec("10.11.1.193", "liyuming", "304624lym", 
		"cd /home/liyuming/scalablecommit/oceanbase\n sh bootstrap.sh");
		System.out.print(res);
	}
}
