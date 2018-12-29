package me.rhin.kingdomraiders.server.manager.map;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import me.rhin.kingdomraiders.server.gameobjects.entity.dungeon.Dungeon.Leaf;

public class Map {

	public String name;
	public ArrayList<String> mapLines;

	public Leaf rootLeaf;

	public Map(String name, ArrayList<String> mapLines) {
		this.name = name;
		this.mapLines = mapLines;
	}

	public void update() {

	}

	// Debug window to view our maps properly.
	public void createDisplayWindow() {
		new DebugWindow();
	}

	public class DebugWindow {

		private int width, height, tileSize;

		private JFrame frame;
		private JPanel panel;
		private Graphics g2;

		public DebugWindow() {
			this.tileSize = 2;

			this.width = (150 * this.tileSize) + 1;
			this.height = (150 * this.tileSize) + 1;
			this.createJFrmae();
		}

		private void createJFrmae() {
			BufferedImage image = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);

			panel = new JPanel();
			panel.setLayout(null);
			panel.setBackground(Color.WHITE);
			panel.setPreferredSize(new Dimension(width, height));

			JLabel drawingArea = new JLabel(new ImageIcon(image));
			drawingArea.setBounds(0, 0, this.width, this.height);
			panel.add(drawingArea);

			g2 = (Graphics2D) image.getGraphics();
			g2.setColor(Color.BLACK);

			frame = new JFrame("Map Debug");
			frame.setResizable(false);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.getContentPane().add(panel);
			frame.pack();
			frame.setVisible(true);
			frame.toFront();

			// Display map tiles
			int y = 0;
			int x = 0;
			for (String s : mapLines) {

				// [1][2][3]
				for (int i = 0; i < s.length(); i++) {
					if (s.charAt(i) == '[') {
						char charID = s.charAt(i + 1);
						int tileID = charID - '0';
						this.drawTileAt(x, y, tileID);
						x++;
					}
				}

				y++;
				x = 0;
			}

			// Display leafs of dungeon
			Random rnd = new Random();
			this.g2.setColor(Color.GREEN);
			ArrayList<Leaf> leafs = rootLeaf.getLeafs();
			for (Leaf l : leafs)
				this.g2.drawRect(l.container.x * 2, l.container.y * 2, l.container.w * 2, l.container.h * 2);

		}

		private void drawTileAt(int x, int y, int tileID) {
			Color color = null;
			x *= this.tileSize;
			y *= this.tileSize;

			if (tileID == 4)
				color = Color.BLACK;
			if (tileID == 5)
				color = Color.GRAY;

			this.g2.setColor(color);
			this.g2.fillRect(x, y, this.tileSize, this.tileSize);

		}
	}
}
