import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Scanner;

public class ReadFile{
	static String desktop = System.getProperty("user.home") + "/Desktop/";

	static File file = new File(desktop + "Assign2Test1.txt");
	private static Rectangle[] rec;

	public static void main(final String[] args) {
		try {
			final BufferedReader br = new BufferedReader(new FileReader(file));
			final BufferedReader br2 = new BufferedReader(new FileReader(file));
			 String strCurrentLine;
			 try {
				 int pointArraySize = 0;
			   while ((strCurrentLine = br.readLine()) != null) {
				   pointArraySize++;
				   System.out.println(strCurrentLine);
			   rec = new Rectangle[pointArraySize];
			   }
			 }catch (NumberFormatException e){
			       System.out.println("not a number"); 
			   } 
			

			int i = 0;
			int xMax = 0;
			int yMax = 0;

			
			while (br2.ready()) {
				final String[] split = br2.readLine().split(",");
				final int x = Integer.parseInt(split[0]);
				final int y = Integer.parseInt(split[1]);
				final int z = Integer.parseInt(split[2]);
				final int s = Integer.parseInt(split[3]);

				xMax = Math.max(x, xMax);
				yMax = Math.max(y, yMax);
				rec[i++] = new Rectangle(x, y,z,s);
			}
			final JFrame frame = new JFrame("Screen");
			final Panel panel = new Panel();
			panel.setPreferredSize(new Dimension(1000,500));
			frame.setContentPane(panel);
			frame.pack();
			frame.setVisible(true);
			frame.repaint();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public static class Panel extends JPanel {

		@Override
		public void paintComponent(final Graphics g) {
			g.setColor(Color.RED);
			for (final Rectangle p : rec) {
				g.drawRect((int) p.getX(), (int) p.getY(), (int) p.height, (int) p.width);
			}
		}

	}

}
