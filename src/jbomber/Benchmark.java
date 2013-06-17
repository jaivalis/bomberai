package jbomber;

import java.util.HashMap;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;

public class Benchmark
{
	private int[] count = {0, 0, 0, 0};
	private int numberOfMarks = 10;

	public static void main (String[] args)
	{
		System.out.println("BENCHMARK STARTED");
		new Benchmark(args);
	}


	public Benchmark(String[] args)
	{
		Main.benchmark = this;
		Main.main(args);
	}

	public void gameOver(Player winner)
	{
		count[winner.type] += 1;
		numberOfMarks --;
		if (numberOfMarks == 0)
			exit();
	}

	public void exit()
	{
		for (int i = 0; i < count.length; i ++)
			System.out.printf("%s won %d times\n", Main.PLAYERTYPE.fromOrdinal(i), count[i]);
		System.exit(0);
	}
}

