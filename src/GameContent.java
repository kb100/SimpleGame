import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.Timer;

public class GameContent extends JPanel implements ActionListener
{
	final int GAME_WIDTH = 500;
	final int GAME_HEIGHT = 500;
	final int PLAYER_HEIGHT = 50;
	final int PLAYER_WIDTH = 25;

	Timer timer;
	Player player;
	SolidRectangle floor;

	// TODO: Keep sorted by order should be painted back to front
	ArrayList<Drawable> drawables;

	public GameContent()
	{
		this.setSize(GAME_WIDTH, GAME_HEIGHT);
		this.setBackground(Color.BLACK);

		drawables = new ArrayList<Drawable>();

		floor = new SolidRectangle(10, 480, 480, 10, Color.GRAY);
		drawables.add(floor);
		
		GameController controller = new GameController();
		this.addKeyListener(controller);
		player = new Player((GAME_WIDTH - PLAYER_WIDTH) / 2, (GAME_HEIGHT - PLAYER_HEIGHT) / 2, PLAYER_WIDTH, PLAYER_HEIGHT, controller);
		drawables.add(player);

		timer = new Timer(20, this);
		timer.start();
	}

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		for (Drawable drawMe : drawables)
		{
			drawMe.draw(g);
		}
	}

	public void actionPerformed(ActionEvent e)
	{

		// not touching floor
		if (!floor.toRectangle().intersects(player.toRectangle()))
		{
			player.ddy = 1;
		}
		else
		{
			player.land();
		}

		player.control();
		player.update();

		adjustFrameIfNecessary();
		repaint();
	}

	public void adjustFrameIfNecessary()
	{
		int dx;
		if ((dx=(GAME_WIDTH - GAME_WIDTH / 3)- player.x ) < 0 || (dx = GAME_WIDTH / 3 - player.x) > 0)
		{
			for(Drawable shiftMe : drawables)
			{
				shiftMe.unconditionalShift(dx, 0);
			}
		}
	}

}
