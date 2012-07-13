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
	final int PLAYER_WIDTH = 20;

	GameController controller;
	Timer timer;
	Player player;
	SolidRectangle floor;

	// TODO: Keep sorted by order should be painted back to front
	ArrayList<Drawable> drawables;

	public GameContent()
	{
		this.setSize(GAME_WIDTH, GAME_HEIGHT);
		setBackground(Color.BLACK);

		drawables = new ArrayList<Drawable>();

		floor = new SolidRectangle(10, 480, 480, 10, Color.GRAY);
		drawables.add(floor);
		
		player = new Player((GAME_WIDTH - PLAYER_WIDTH) / 2, (GAME_HEIGHT - PLAYER_HEIGHT) / 2, PLAYER_WIDTH, PLAYER_HEIGHT);
		drawables.add(player);

		timer = new Timer(20, this);
		timer.start();

		controller = new GameController();
		addKeyListener(controller);

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

		if (controller.left) player.ddx = -1;
		else if (controller.right) player.ddx = 1;
		else player.ddx = 0;

		if (controller.jump) player.jump();
		player.update();

		adjustFrameIfNecessary();
		repaint();
	}

	public void adjustFrameIfNecessary()
	{
		int xShift = player.x - (GAME_WIDTH - GAME_WIDTH / 3);
		if (xShift > 0)
		{
			player.unconditionalShift(-xShift, 0);
			floor.x -= xShift;
		}
		else
		{
			xShift = GAME_WIDTH / 3 - player.x;
			if (xShift > 0)
			{
				player.unconditionalShift(xShift, 0);
				floor.x += xShift;
			}
		}
	}

}
