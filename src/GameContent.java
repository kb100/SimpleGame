import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameContent extends JPanel implements ActionListener
{
	final int GAME_WIDTH = 500;
	final int GAME_HEIGHT = 500;
	final int PLAYER_HEIGHT = 50;
	final int PLAYER_WIDTH = 20;

	GameController controller;
	Timer timer;
	Player player;
	Rectangle floor;

	public GameContent()
	{
		this.setSize(GAME_WIDTH, GAME_HEIGHT);
		setBackground(Color.BLACK);

		player = new Player((GAME_WIDTH - PLAYER_WIDTH) / 2, (GAME_HEIGHT - PLAYER_HEIGHT) / 2, PLAYER_WIDTH, PLAYER_HEIGHT);
		floor = new Rectangle(10, 480, 480, 10);

		timer = new Timer(20, this);
		timer.start();
		
		controller = new GameController();
		addKeyListener(controller);

	}

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		player.draw(g);
		g.setColor(Color.WHITE);
		g.fillRect(floor.x, floor.y, floor.width, floor.height);
	}

	public void actionPerformed(ActionEvent e)
	{

		// not touching floor
		if (!floor.intersects(player.toRectangle()))
		{
			player.ddy = 1;
		}
		else
		{
			player.land();
		}

		if(controller.left) player.ddx = -1;
		else if(controller.right) player.ddx = 1;
		else player.ddx = 0;
		
		if(controller.jump) player.jump();
		player.update();

		repaint();
	}

	
}
