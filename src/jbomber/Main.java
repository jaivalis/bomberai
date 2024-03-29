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

public class Main extends BasicGame {

    private final boolean musicOn = false;

	// Do not forget to set this to false before we turn this in
    private final boolean MUTE_SOUND = true;

    private final int TIME_MAX = 10000;

    private final int NUMBER_OF_PLAYER_TYPES = 4;

    /** Enum for the gamestate, feel free to use. */
    public enum GAMESTATE { MENU, RUNNING, MATCH_RESULTS }

    private int gameState = 0;

    /* Game State List for Reference
     *  0 - Menu
     *  1 - In Game
     *
     * Future States Needed
     *
     *  2 - Match Results
     */

    //0 - off 1 - human 2 - SimpleAI 3 - MapClearAI
	public enum PLAYERTYPE 
	{
		OFF, HUMAN, SIMPLE_AI, MAP_CLEAR_AI;
		private static PLAYERTYPE[] allValues = values();
    	public static PLAYERTYPE fromOrdinal(int n) {return allValues[n];}
	}

    private int playerType[] = {
		PLAYERTYPE.SIMPLE_AI.ordinal(),
		PLAYERTYPE.MAP_CLEAR_AI.ordinal(),
		PLAYERTYPE.OFF.ordinal(),
		PLAYERTYPE.OFF.ordinal()};
    
//    private GenericAI aiArr[] = {null, null, null, null };
    private HashMap<Player, GenericAI> aiMap; 
    
    //Menu Resources
    private Image bgSmall;
    private Image bgBig;
    private int bgX = 0;
    private int bgY = 0;
    private Image playButton;
    private Image winnerImage;
    private Image optionsButton;
    private Image quitButton;
    private Image backButton;
    private Image title;
    private Image cpuCap1, cpuCap2, humCap, offCap;
    private int menuMouseX, menuMouseY;
    private Rectangle play, options, quit, back;
    //private Rectangle p1, p2, p3, p4;
    private Rectangle[] playerRects = new Rectangle[4];

    //Effects Resources
    private Image fog;
    private float fogX = 0;
    
    //These affect the whole screen offsets (for shaking)
    public int jitterX = 16;
    public int jitterY = 0;
    private boolean shake;
    private int shakeMagnitude;
    private boolean shakeRight = true;

    //Audio Resources
    private Sound explosion, bombup, fireup;
    private Music bombsong;

    public Player whiteBomber, blackBomber, redBomber, blueBomber;
    public Player[] players;

    public static MersenneTwisterFast mt = new MersenneTwisterFast();

    public SpriteSheet tileset;
    public SpriteSheet bombImage;
    public SpriteSheet deathAnim;
    
    private Input input;
    private boolean changingOptions = false;

    private int time;
    
    public Map theMap;

	public static Benchmark benchmark;

    public Main() {
        //Changed from jbomber to avoid confusion with other projects.
        //Will most likely redo title graphic to reflect this change in an
        //upcoming revision.
        super("Bomber AI");
    }


    public static void main(String[] arguments) {
        try {
            AppGameContainer app = new AppGameContainer(new Main());
            app.setDisplayMode(640, 480, false);
            app.setShowFPS(false);
            app.setTargetFrameRate(benchmark == null ? 75 : 9999999);
            app.setVSync(false);
            app.setFullscreen(false);
            app.start();
        }
        catch (SlickException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init(GameContainer container) throws SlickException
    {
        //Load AI engine
        aiMap = new HashMap<Player, GenericAI>();
        
//        aiArr[0] = new SimpleAI();
//        aiArr[1] = new SimpleAI();
//        aiArr[2] = new SimpleAI();
        
        //Menu Graphics Loading
        title = new Image("data/menu/title.png");
        playButton = new Image("data/menu/button_play.png");
        winnerImage = new Image("data/menu/winner.png");
        optionsButton = new Image("data/menu/button_options.png");
        quitButton = new Image("data/menu/button_quit.png");
        backButton = new Image("data/menu/button_back.png");
        bgSmall = new Image("data/menu/background_small.png");
        bgBig = new Image("data/menu/background_big.png");
        cpuCap1 = new Image("data/menu/cpu_caption1.png");
        cpuCap2 = new Image("data/menu/cpu_caption2.png");
        humCap = new Image("data/menu/human_caption.png");
        offCap = new Image("data/menu/off_caption.png");

        //Animations Loading
        bombImage = new SpriteSheet("data/bomb.png", 32, 32);
        deathAnim = new SpriteSheet("data/death_animation.png", 32, 32);

        //TileSet Loading
        tileset = new SpriteSheet("data/tileset.png", 32, 32);

        //Effect Loading
        fog = new Image("data/fog.png");

        //Sound Loading
        explosion = new Sound("data/explosion.wav");       
        bombup = new Sound("data/bombup.wav");
        fireup = new Sound("data/fireup.wav");

        //Rectangles to click for menu interaction
        play = new Rectangle(200, 140, playButton.getWidth(), playButton.getHeight());
        options = new Rectangle(200, 240, optionsButton.getWidth(), optionsButton.getHeight());
        quit = new Rectangle(200, 340, quitButton.getWidth(), quitButton.getHeight());
        back = new Rectangle(200, 340, backButton.getWidth(), backButton.getHeight());

        playerRects[0] = new Rectangle(130, 220, 100, 50);
        playerRects[1] = new Rectangle(230, 220, 100, 50);
        playerRects[2] = new Rectangle(330, 220, 100, 50);
        playerRects[3] = new Rectangle(430, 220, 100, 50);
        
        //Music Loading
        if (musicOn) {
            bombsong = new Music("data/bombsong.ogg");
            bombsong.loop();
        }

        //Input Loading
        input = container.getInput();
    }

    @Override
    public void update(GameContainer container, int delta) throws SlickException
    {
        if (gameState == 0) {
            checkInputMenu(container);
			if (benchmark != null)
			{
				gameState = GAMESTATE.RUNNING.ordinal();
				newRound(playerType);
			}
        }
        if (gameState == 1) {
            if(++time > TIME_MAX)
            {
                System.out.println("Time is up, round ended as draw");
                if (benchmark != null)
                    benchmark.gameOver(0);
                gameState = GAMESTATE.MENU.ordinal();
            }
            if(time % 1000 == 0)
                System.out.printf("\rTime now is %d", time);
            checkInputGame(container);
            //Check on all the bombs
            checkBombs();
            //Check to see if anyone has been killed
            checkFire();
            //Check input or AI for each player
            for (Player p : players)
                checkPlayer(p);
            //Shift any players currently in transition between tiles
            for (Player p : players)
                p.shift(this);
            //Update the screen shake effect if necessary
            checkShake();
            //Update the fog effect
            fogX += -0.3f;
            if (fogX < -640) { fogX = 0; }
        }
    }

    /**
     *
     * @param container
     * @param g
     * @throws SlickException
     */
    @Override
    public void render(GameContainer container, Graphics g) throws SlickException {
		if (benchmark != null)
			return;
        if (gameState == 0) {
            drawMenuBackground(g);
            drawMenuButtons(g);
            // If a winner is known
            drawWinner(g);
            g.drawImage(title, 50, 0);
        }
        if (gameState == 1) {
            theMap.draw(this);
            drawFire(g);
            for (Player p : players)
                p.draw(g, this);
            g.drawImage(fog, fogX + jitterX, 0);
            g.drawImage(fog, fogX + 640 + jitterX, 0);
            //These commented out lines are for testing the AI
//            drawTarget(g, whiteBomber);
//            drawTarget(g, blueBomber);
//            drawTarget(g, blackBomber);
//            drawTarget(g, redBomber);
//            drawPlayerPhase(g, whiteBomber);
//            drawPlayerPhase(g, blackBomber);
//            drawPlayerPhase(g, redBomber);
//            drawPlayerPhase(g, blueBomber);
        }
    }

    private void drawPlayerPhase(Graphics g, Player player)
    {
        g.drawString("" + player.getPhase(), player.getX() * 32, player.getY() * 32);
    }

    private void changeMusic(int songNumber) throws SlickException
    {
        if (musicOn)
        {
            bombsong.stop();
            if (songNumber == 1)
            {
                bombsong = new Music("data/bombsong.ogg");
            }
            if (songNumber == 2)
            {
                bombsong = new Music("data/cavesong.ogg");
            }
            bombsong.loop();
        }
    }

    private void checkInputMenu(GameContainer container) throws SlickException
    {
        if (input.isKeyPressed(Input.KEY_ESCAPE))
        {
            container.exit();
        }
        if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON))
        {
            menuMouseX = input.getMouseX();
            menuMouseY = input.getMouseY();
            Rectangle mouseClicker = new Rectangle(menuMouseX, menuMouseY, 1,1);
            if (changingOptions)
            {
                if (mouseClicker.intersects(back))
                {
                    int playersOn = 0;
                    for (int i = 0; i < 4; i++)
                    {
                        if (playerType[i] != 0)
                        {
                            playersOn += 1;
                        }
                    }
                    if (playersOn >= 2)
                    {
                        changingOptions = false;
                    }
                }
                for (int i=0 ;i<4; i++)
                {
                    if(mouseClicker.intersects(playerRects[i]))
                    {
                        // Increment and maybe reset player type
                        playerType[i] = ++playerType[i] % NUMBER_OF_PLAYER_TYPES;
                    }
                }
            }
            else
            {
                if (mouseClicker.intersects(play))
                {
                    gameState = 1;
                    changeMusic(2);
                    newRound(playerType);
                }
                if (mouseClicker.intersects(options))
                {
                    changingOptions = true;
                }
                if (mouseClicker.intersects(quit))
                {
                    container.exit();
                }
            }
        }
    }

    private void checkShake()
    {
        if (shake)
        {
            if (shakeRight)
            {
                jitterX += shakeMagnitude * 2;
                if (jitterX > 16 + shakeMagnitude)
                {
                    shakeRight = false;
                }
            }
            else
            {
                jitterX += -shakeMagnitude * 2;
                if (jitterX < 16 - shakeMagnitude)
                {
                    shakeRight = true;
                }
                else
                {
                    shake = false;
                }
            }
        }
        else
        {
            shakeRight = true;
            jitterX = 16;
            shakeMagnitude = 0;
        }
    }

    private void drawMenuBackground(Graphics g) {
        bgX += 1;
        for (int x = 0; x < 8; x++)
        {
            for (int y = 0; y < 8; y++)
            {
                g.drawImage(bgSmall, x * 80, y * 60);
            }
        }
        for (int x = 0; x < 8; x++)
        {
            for (int y = 0; y < 4; y++)
            {
                if (y % 2 == 0)
                {
                    g.drawImage(bgBig, x * 160 + bgX - 160, y * 120);
                }
                else
                {
                    g.drawImage(bgBig, x * 160 - bgX - 160, y * 120);
                }
            }
        }
        if (bgX > 160)
        {
            bgX = 0;
        }
    }

    private void drawWinner(Graphics g)
    {
        if(players != null && getPlayersAliveCount() > 0)
        {
            g.drawImage(winnerImage, 50, 155);
            for(Player player : players)
            {
                if(player.getAlive())
                {
                    // Draw player and break
                    tileset.getSprite(1, 1).draw(90, 210, 2.0f);
                    switch (player.getPID())
                    {
                        case 2: 
                            tileset.getSprite(0, 1).draw(90, 210, 2.0f, new Color(50, 50, 50));
                        break;
                        case 3:
                            tileset.getSprite(0, 1).draw(90, 210, 2.0f, new Color(255, 50, 50));
                        break;
                        case 4: 
                            tileset.getSprite(0, 1).draw(90, 210, 2.0f, new Color(50, 50, 255));
                        break;
                    }
                    break;
                }
            }
        }
    }
    
    private void drawMenuButtons(Graphics g)
    {
        if (changingOptions)
        {
            g.drawImage(backButton, 200, 340);
            //Draw large players for player type adjustment
            tileset.getSprite(1, 1).draw(150, 150, 2.0f);
            tileset.getSprite(1, 1).draw(250, 150, 2.0f);
            tileset.getSprite(1, 1).draw(350, 150, 2.0f);
            tileset.getSprite(1, 1).draw(450, 150, 2.0f);
            tileset.getSprite(0, 1).draw(250, 150, 2.0f, new Color(50, 50, 50));
            tileset.getSprite(0, 1).draw(350, 150, 2.0f, new Color(255, 50, 50));
            tileset.getSprite(0, 1).draw(450, 150, 2.0f, new Color(50, 50, 255));
            for (int humanCheck = 0; humanCheck < 4; humanCheck ++)
            {
                if (playerType[humanCheck] == 1)
                {
                    g.drawImage(humCap, humanCheck * 100 + 130, 200);
                }
                if (playerType[humanCheck] == 2)
                {
                    g.drawImage(cpuCap1, humanCheck * 100 + 130, 200);
                }
                if (playerType[humanCheck] == 3)
                {
                    g.drawImage(cpuCap2, humanCheck * 100 + 130, 200);
                }
                if (playerType[humanCheck] == 0)
                {
                    g.drawImage(offCap, humanCheck * 100 + 130, 200);
                }
            }
        }
        else
        {
            g.drawImage(playButton, 200, 150);
            g.drawImage(optionsButton, 200, 240);
            g.drawImage(quitButton, 200, 340);
        }
    }

    // initialize board
    private void newRound(int[] playerType)
	{
		System.out.println("newRound called");
        theMap = new Map(this);

        // reset the time
        time = 0;

        //Place Players
        whiteBomber = new Player(1, 1, 1, Color.white, playerType[0]);
        blackBomber = new Player(17, 1, 2, Color.black, playerType[1]);
        redBomber = new Player(17, 13, 3, Color.red, playerType[2]);
        blueBomber = new Player(1, 13, 4, Color.blue, playerType[3]);

        players = new Player[4];
        players[0] = whiteBomber;
        players[1] = blackBomber;               
        players[2] = redBomber;
        players[3] = blueBomber;

        // TODO: A menu should be added to choose AI-types
        for (Player player : players)
        {
            // Type 2 is SimpleAI
            if(player.getType() == 2)
                aiMap.put(player, new SimpleAI(this, player));
            // Type 3 is MapClearAI
            else if (player.getType() == 3)
                aiMap.put(player, new MapClearAI(this, player));
        }

        // aiMap.put(whiteBomber, new SimpleAI(this, whiteBomber));
        // aiMap.put(blackBomber, new SimpleAI(this, blackBomber));
        // aiMap.put(redBomber, new SimpleAI(this, redBomber));
        // aiMap.put(blueBomber, new SimpleAI(this, blueBomber));
    }

    private void makeExplosion(int locX, int locY, int size, boolean up, boolean right, boolean left, boolean down)
    {
        shake = true;
        shakeMagnitude += 1;
        int[][] explodefield = new int[19][15];
        /* Diagram to show which numbers equal which directions (confusing, I know)
         *     5
         *     1
         * 8 4 0 2 6
         *     3
         *     7
        */
        for (int i = locX - 1; i >= locX - size; i--)
        {
            if (i >= 0 && i < 19)
            {
                if (left)
                {
                    switch( theMap.board[i][locY] )
                    {
                        case 0:
                        {
                            explodefield[i][locY] = 4;
                            break;
                        }
                        case 1:
                        {
                            left = false;
                            break;
                        }
                        case 2:
                        {
                            spawnPowerUps(i, locY);
                            explodefield[i][locY] = 8;
                            left = false;
                            break;
                        }
                        case 3:
                        {
                            if (theMap.bombs[i][locY] != null)
                            {
                                boolean[] b = {true, false, true, true};
                                theMap.bombs[i][locY].setDirections(b);
                                theMap.bombs[i][locY].explode();
                                left = false;
                            }
                            else
                            {
                                explodefield[i][locY] = 4;
                            }
                            break;
                        }
                    }
                }
            }
        }
        for (int i = locX + 1; i <= locX + size; i++)
        {
            if (i >= 0 && i < 19)
            {
                if (right)
                {
                    switch( theMap.board[i][locY] )
                    {
                        case 0:
                        {
                            explodefield[i][locY] = 2;
                            break;
                        }
                        case 1:
                        {
                            right = false;
                            break;
                        }
                        case 2:
                        {
                            spawnPowerUps(i, locY);
                            explodefield[i][locY] = 6;
                            right = false;
                            break;
                        }
                        case 3:
                        {
                            if (theMap.bombs[i][locY] != null)
                            {
                                boolean[] b = {true, true, false, true};
                                theMap.bombs[i][locY].setDirections(b);
                                theMap.bombs[i][locY].explode();
                                right = false;
                            }
                            else
                            {
                                explodefield[i][locY] = 2;
                            }
                            break;
                        }
                    }
                }
            }
        }
        for (int i = locY - 1; i >= locY - size; i--)
        {
            if (i >= 0 && i < 19)
            {
                if (up)
                {
                    switch( theMap.board[locX][i] )
                    {
                        case 0:
                        {
                            explodefield[locX][i] = 1;
                            break;
                        }
                        case 1:
                        {
                            up = false;
                            break;
                        }
                        case 2:
                        {
                            spawnPowerUps(locX, i);
                            explodefield[locX][i] = 5;
                            up = false;
                            break;
                        }
                        case 3:
                        {
                            if (theMap.bombs[locX][i] != null)
                            {
                                boolean[] b = {true, true, true, false};
                                theMap.bombs[locX][i].setDirections(b);
                                theMap.bombs[locX][i].explode();
                                up = false;
                            }
                            else
                            {
                                explodefield[locX][i] = 1;
                            }
                            break;
                        }
                    }
                }
            }
        }
        for (int i = locY + 1; i <= locY + size; i++)
        {
            if (i >= 0 && i < 19)
            {
                if (down)
                {
                    switch( theMap.board[locX][i] )
                    {
                        case 0:
                        {
                            explodefield[locX][i] = 3;
                            break;
                        }
                        case 1:
                        {
                            down = false;
                            break;
                        }
                        case 2:
                        {
                            spawnPowerUps(locX, i);
                            explodefield[locX][i] = 7;
                            down = false;
                            break;
                        }
                        case 3:
                        {
                            if (theMap.bombs[locX][i] != null)
                            {
                                boolean[] b = {false, true, true, true};
                                theMap.bombs[locX][i].setDirections(b);
                                theMap.bombs[locX][i].explode();
                                down = false;
                            }
                            else
                            {
                                explodefield[locX][i] = 3;
                            }
                            break;
                        }
                    }
                }
            }
        }
        explodefield[locX][locY] = 10;
        for (int x = 0; x < 19; x++)
        {
            for (int y = 0; y < 15; y++)
            {
                if (explodefield[x][y] != 0)
                {
                    if (x - 1 >= 0)
                    {
                        if (explodefield[x][y] == 4)
                        {
                            if (explodefield[x-1][y] == 0)
                            {
                                explodefield[x][y] = 8;
                            }
                        }
                    }
                    if (x + 1 < 19)
                    {
                        if (explodefield[x][y] == 2)
                        {
                            if (explodefield[x+1][y] == 0)
                            {
                                explodefield[x][y] = 6;
                            }
                        }
                    }
                    if (y - 1 >= 0)
                    {
                        if (explodefield[x][y] == 1)
                        {
                            if (explodefield[x][y-1] == 0)
                            {
                                explodefield[x][y] = 5;
                            }
                        }
                    }
                    if (y + 1 < 15)
                    {
                        if (explodefield[x][y] == 3)
                        {
                            if (explodefield[x][y+1] == 0)
                            {
                                explodefield[x][y] = 7;
                            }
                        }
                    }
                    theMap.fire[x][y] = new Fire(explodefield[x][y]);
                }
            }
        }
        updateFireSprites();
    }

    private void updateFireSprites()
    {
        //Update the fire graphics as necessary
        for (int x = 0; x < 19; x++)
        {
            for (int y = 0; y < 15; y++)
            {
                boolean up = false;
                boolean down = false;
                boolean right = false;
                boolean left = false;
                if (theMap.fire[x][y] != null)
                {
                    if (x - 1 >= 0)
                    {
                        if (theMap.fire[x-1][y] != null)
                        {
                            left = true;
                        }
                    }
                    if (x + 1 < 19)
                    {
                        if (theMap.fire[x+1][y] != null)
                        {
                            right = true;
                        }
                    }
                    if (y + 1 < 15)
                    {
                        if (theMap.fire[x][y+1] != null)
                        {
                            down = true;
                        }
                    }
                    if (y - 1 >= 0)
                    {
                        if (theMap.fire[x][y-1] != null)
                        {
                            up = true;
                        }
                    }
                    if (theMap.fire[x][y] != null)
                    {
                        if (up && !down && !left && !right)
                        {
                            theMap.fire[x][y].setDirection(7);
                        }
                        if (up && down && !left && !right)
                        {
                            theMap.fire[x][y].setDirection(3);
                        }
                        if (!up && !down && !left && right)
                        {
                            theMap.fire[x][y].setDirection(8);
                        }
                        if (!up && !down && left && right)
                        {
                            theMap.fire[x][y].setDirection(4);
                        }
                        if ((up || down) && (left || right))
                        {
                            theMap.fire[x][y].setDirection(10);
                        }
                    }
                }
            }
        }
    }

    private void spawnPowerUps(int x, int y) {
        int chanceForPowerUp = mt.nextInt(5);
        switch(chanceForPowerUp) {
            case 1: {
                theMap.board[x][y] = 5;
                break;
            }
            case 2: {
                theMap.board[x][y] = 6;
                break;
            }
            default: {
                theMap.board[x][y] = 0;
                break;
            }
        }
    }

    //AI is using poor logic now, but can use bombs    

    private void drawTarget(Graphics g, Player player)
    {
        if (player.getAlive())
        {
            g.setColor(player.getColor());
            g.drawRect(player.getSafeSpot()[0] * 32 + jitterX, player.getSafeSpot()[1] * 32 + jitterY, 32, 32);
        }
    }

    private void checkInputGame(GameContainer container) throws SlickException
    {
        if (input.isKeyPressed(Input.KEY_ESCAPE))
        {
            changeMusic(1);
            gameState = 0;
        }
        if (input.isKeyPressed(Input.KEY_F2))
        {
            newRound(playerType);
        }
        if (input.isKeyPressed(Input.KEY_F4))
        {
            container.setFullscreen( ! container.isFullscreen());
        }
        if (whiteBomber != null)
        {
            if (whiteBomber.getHuman() && whiteBomber.getAlive())
            {
                if (input.isKeyDown(Input.KEY_W))
                {
                    whiteBomber.move(0, -1, this);
                }
                if (input.isKeyDown(Input.KEY_A))
                {
                    whiteBomber.move(-1, 0, this);
                }
                if (input.isKeyDown(Input.KEY_S))
                {
                    whiteBomber.move(0, 1, this);
                }
                if (input.isKeyDown(Input.KEY_D))
                {
                    whiteBomber.move(1, 0, this);
                }
                if (input.isKeyDown(Input.KEY_SPACE))
                {
                    whiteBomber.placeBomb(this);
                }
            }
        }
        if (blackBomber != null)
        {
            if (blackBomber.getHuman() && blackBomber.getAlive())
            {
                if (input.isKeyDown(Input.KEY_I))
                {
                    blackBomber.move(0, -1, this);
                }
                if (input.isKeyDown(Input.KEY_J))
                {
                    blackBomber.move(-1, 0, this);
                }
                if (input.isKeyDown(Input.KEY_K))
                {
                    blackBomber.move(0, 1, this);
                }
                if (input.isKeyDown(Input.KEY_L))
                {
                    blackBomber.move(1, 0, this);
                }
                if (input.isKeyDown(Input.KEY_SEMICOLON))
                {
                    blackBomber.placeBomb(this);
                }
            }
        }
    }

    private void checkBombs()
    {
        for (int x = 0; x < 19; x ++)
        {
            for (int y = 0; y < 15; y++)
            {
                if (theMap.bombs[x][y] != null)
                {
                    if (theMap.bombs[x][y].getExploded())
                    {
                        if ( ! explosion.playing())
                        {
							if (!MUTE_SOUND)
								explosion.play();
                        }
                        makeExplosion(x, y, theMap.bombs[x][y].getSize(),
                        theMap.bombs[x][y].getDirections()[0],
                        theMap.bombs[x][y].getDirections()[1],
                        theMap.bombs[x][y].getDirections()[2],
                        theMap.bombs[x][y].getDirections()[3]);
                        theMap.board[x][y] = 0;
                        theMap.bombs[x][y] = null;
                    }
                    else
                    {
                        theMap.bombs[x][y].update();
                    }
                }
            }
        }
    }

    private void checkFire()
    {
        for (int x = 0; x < 19; x ++)
        {
            for (int y = 0; y < 15; y++)
            {
                if (theMap.fire[x][y] != null)
                {
                    if (theMap.fire[x][y].getDead())
                    {
                        theMap.fire[x][y] = null;
                        updateFireSprites();
                    }
                    else
                    {
                        theMap.fire[x][y].update();
                        int i = theMap.players[x][y];
                        if (i >= 1 && i <= 4)
                        {
                            flushPlayerReferences(i);
                            players[i - 1].setAlive(false);
                            theMap.players[x][y] = 0;
                            if (getPlayersAliveCount() <= 1)
							{
								if (benchmark != null)
									benchmark.gameOver(
										getFirstLivingPlayer());
                                gameState = GAMESTATE.MENU.ordinal();
							}
                        }
                    }
                }
            }
        }
    }

    /** Returns the number of players that are alive. */
    public int getPlayersAliveCount()
    {
        int c = 0;
        for (Player p : players)
            if (p.getAlive())
                c++;
        return c;
    }

	/** Returns the first player instance that is still alive or null
	* otherwise */
	public Player getFirstLivingPlayer()
	{
        for (Player p : players)
            if (p.getAlive())
				return p;
		return null;
	}

    private void checkPlayer(Player player) {
        if (player.getAlive()) {
            theMap.players[player.getX()][player.getY()] = player.getPID();
            if (player.getHuman()) {
                if (player.getClock() > 0) {
                    player.setClock(player.getClock()-1);
                }
            }
            else {
                aiMap.get(player).abstractUpdateAI();
//                playerAI.updateAI(player, this);
            }
        }
    }

    private void drawFire(Graphics g)
    {
        // A diagram to show which numbers coordinate to which bomb direction graphic
        /*     5
         *     1
         * 8 4 0 2 6
         *     3
         *     7
        */
        for (int x = 0; x < 19; x++)
        {
            for (int y = 0; y < 15; y++)
            {
                if (theMap.fire[x][y] != null)
                {
                    switch(theMap.fire[x][y].getDirection())
                    {
                        case 10:
                        {
                            tileset.getSprite(8, 0).draw(x * 32 + jitterX, y * 32 + jitterY);
                            break;
                        }
                        case 1:
                        {
                            tileset.getSprite(12, 0).draw(x * 32 + jitterX, y * 32 + jitterY);
                            break;
                        }
                        case 2:
                        {
                            tileset.getSprite(9, 0).draw(x * 32 + jitterX, y * 32 + jitterY);
                            break;
                        }
                        case 3:
                        {
                            tileset.getSprite(13, 0).draw(x * 32 + jitterX, y * 32 + jitterY);
                            break;
                        }
                        case 4:
                        {
                            tileset.getSprite(7, 0).draw(x * 32 + jitterX, y * 32 + jitterY);
                            break;
                        }
                        case 5:
                        {
                            tileset.getSprite(11, 0).draw(x * 32 + jitterX, y * 32 + jitterY);
                            break;
                        }
                        case 6:
                        {
                            tileset.getSprite(10, 0).draw(x * 32 + jitterX, y * 32 + jitterY);
                            break;
                        }
                        case 7:
                        {
                            tileset.getSprite(14, 0).draw(x * 32 + jitterX, y * 32 + jitterY);
                            break;
                        }
                        case 8:
                        {
                            tileset.getSprite(6, 0).draw(x * 32 + jitterX, y * 32 + jitterY);
                            break;
                        }
                    }
                }
            }
        }
    }

    private void flushPlayerReferences(int PID) {
        for (int x = 0; x < 19; x++) {
            for (int y = 0; y < 15; y++) {
                if (theMap.players[x][y] == PID) {
                    theMap.players[x][y] = 0;
                }
            }
        }
    }

    public Input getInput() { return input; }

    public void playSound(String sound) 
	{
		if (!MUTE_SOUND)
		{
			if (sound.equals("bombup")) { bombup.play(); }
			if (sound.equals("fireup")) { fireup.play(); }
		}
    }
}
