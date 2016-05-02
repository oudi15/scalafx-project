package AppManager

import scala.util.control.Breaks._
import scala.collection.mutable.ArrayBuffer
import scalafx.animation._
import scalafx.animation.Interpolator
import scalafx.scene.media._
import scala.math.random
import scala.util.control.Breaks
import scalafx.Includes._
import scalafx.beans.property.{DoubleProperty}
import scalafx.application.JFXApp
import scalafx.geometry.{Insets, VPos}
import scalafx.scene.{Node, Scene, Group, Cursor}
import scalafx.scene.control.{Label, Button}
import scalafx.scene.layout.{HBox, VBox, BorderPane, StackPane}
import scalafx.scene.paint.{Color, CycleMethod, LinearGradient, Stop}
import scalafx.scene.text.Font
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.{MouseEvent, KeyCode, KeyEvent}
import scalafx.scene.shape.{Circle, Rectangle, Polygon}
import scalafx.event.ActionEvent
// The following are the imports needed for StateManager.
import stateManager._
import stateManager.appState._
import mapManager._
import mapManager.tile._
import EntityManager._

// Entry point.
object AppManager extends JFXApp
{
  val stateManager = new StateManager

  stage = new JFXApp.PrimaryStage
  {
    title = "GOC"
    width = 768
    height = 580
  }
  // Push the menu state (also called a scene) into the stack.
  stateManager.pushState(new MenuState(stage, stateManager))
  // Peek at that state, and display it as the current scene.
  stage.scene = stateManager.peekState()
}

class MenuState(stage: JFXApp.PrimaryStage, stateManager: StateManager) extends AppState
{
 fill = Color.Gray

  root = new Group
  {
    children = List(
      new ImageView
      {
        image = new Image("/scalafx/images/logo.png")
        layoutX = 0
        layoutY = 0
		fitHeight = 580
		fitWidth = 780
        preserveRatio = true
        smooth = true
      },
      new HBox
      {
		//style = "-fx-background-color: gray"
        layoutX = 300
        layoutY = 500
        spacing = 20
        children = List(
          new Button
          {
            text = "Play"
            onAction = handle
            {
              stateManager.pushState(new GameState(stage, stateManager))
              stage.scene = stateManager.peekState()
            }
          },
          new Button
          {
            text = "Instructions"
            onAction = handle
            {
              stateManager.pushState(new InstructionState(stage, stateManager))
              stage.scene = stateManager.peekState()
            }
          },
          new Button
          {
            text = "Exit"
            onAction = handle
            {
              stage.close()
            }
          }
        )
      }
    )
  }
}



class GameState(stage: JFXApp.PrimaryStage, stateManager: StateManager) extends AppState
{
	//var myT = new Array[Timeline](6)
	var enemyDamage = 50
	var enemySpeed = 10
	var enemyArray = new Array[ImageView](6) //enemies array images
	var enemiesX = new Array[Int](6)
	var enemiesY = new Array[Int](6)
	var bullets = new Array[Rectangle](10)
	var bcount = 0
	var MyHero = new Hero
	var MyEnemies = new Array[Enemy](6)
	var currImage = 1
	 val mapLength = 24
      val mapHeight = 17
	  
	 //Bullet coordinates
	 var bulletX = new Array[DoubleProperty](10)
	 var bulletY = new Array[DoubleProperty](10)
	 var visiblity = false
	 var fireAgain = true
  // The coordinates of the hero
  var heroX = new DoubleProperty
  var heroY = new DoubleProperty

  // The drag anchor for the hero
  var heroDragAnchorX: Double = _
  var heroDragAnchorY: Double = _

  // The initial translate property for the hero
  var initHeroTranslateX: Double = _
  var initHeroTranslateY: Double = _
 
  // Draw the background
  val imageButton = new ImageView
  {
    image = new Image("/scalafx/images/background.png")
    layoutX = 50
    layoutY = 150
    scaleX = 200
    scaleY = 300
    preserveRatio = true
    smooth = true
  }

  // Draw the hero
  	val hero:ImageView = new ImageView
  {
    image = new Image("/scalafx/images/down1.png")
    layoutX = 32
    layoutY = 32
    scaleX = 1
    scaleY = 1
    preserveRatio = true
    smooth = true
    cursor = Cursor.HAND

    translateX <== heroX
    translateY <== heroY

    onMousePressed = (me: MouseEvent) =>
    {
      initHeroTranslateX = hero.translateX()
      heroDragAnchorX = me.sceneX
      initHeroTranslateY = hero.translateY()
      heroDragAnchorY = me.sceneY
    }

    onMouseDragged = (me: MouseEvent) =>
    {
      val dragX = me.sceneX - heroDragAnchorX
      heroX() = initHeroTranslateX + dragX
      val dragY = me.sceneY - heroDragAnchorY
      heroY() = initHeroTranslateY + dragY
    }
  }
  
  
 

   // Create bullet
   def createBullet(): Rectangle =
   {
   var bullet:Rectangle = new Rectangle {
		fill= Color.YELLOW
		stroke= Color.RED
		visible = false
		width = 10
		height= 10
		}
		
		return bullet
		 //Bullet timeline animation
		}
		
		//initialize bullet transition
	
  
 

   // These measurements are less than the stage size of 800 x 600 because
   // the window borders are included in the stage size.
   val topWall = new Rectangle
   {
     x = 0
     y = 0
     width = 768
     height = 1
   }
   val bottomWall = new Rectangle
   {
     x = 0
     y = 562
     width = 768
     height = 1
   }
   val leftWall = new Rectangle
   {
     x = 0
     y = 0
     width = 1
     height = 544
   }
   val rightWall = new Rectangle
   {
     x = 784
     y = 0
     width = 1
     height = 544
   }

   // imageBuffer is used to contain all the images draw on the screen in order.
	var imageBuffer = ArrayBuffer[Node]()
	var mapManager = new MapManager {}
	var tileMap = mapManager.createRandomTileMap()
	var flatTileMap = tileMap.getFlattenedImageArray()

   // Add the tileMap in the imageBuffer.
   for(i <- 0 to (flatTileMap.length - 1))
   {
     imageBuffer += flatTileMap(i)
   }

   // Add the edge boundaries into the imageBuffer.
   imageBuffer += topWall
   imageBuffer += bottomWall
   imageBuffer += leftWall
   imageBuffer += rightWall
	
	//generate enemies
	def generateEnemies()
	{
		for(i <- 0 to 5)
			{
				enemyArray(i):ImageView 
				}
   
			var i = 0
		var ady: Double = 512
	for( i <- 0 to 5)
	{
		
		var adx: Double = 736
		ady -= 64
		do 
		{
			adx -= 32
		}while(!tileMap.getTileAtPixel((adx.toInt),(ady.toInt)).isWalkable)
    enemyArray(i)= new ImageView 
	{
	image = new Image("/scalafx/images/enemy1.png")
    layoutX = adx
    layoutY = ady
    scaleX = 1
    scaleY = 1
	translateX <== enemiesX(i)
	translateY <== enemiesY(i)
    preserveRatio = false
    smooth = true
    cursor = Cursor.HAND
	}
	
	
	}
		}
	
		generateEnemies()
	
	//add enemies to imageBuffer
	
	var j = 0
	for(j <- 0 to 5)
   {
     imageBuffer += enemyArray(j)
   }
   
   
	
   imageBuffer += hero
   
   //Hero shoot
	var m = 0
	for(m <- 0 to 9){
	
    bullets(m) = createBullet()
	
  }
   def heroShoot()
   { 
		bullets(bcount).layoutX = hero.translateX.toDouble + 64
		bullets(bcount).layoutY = hero.translateY.toDouble + 48
		bullets(bcount).visible = true
		//timeline.play()
		// bullet.visible = false
   }
   
   for(m <- 0 to 9)
   {
	imageBuffer += bullets(m)
	}
	//border collision
	// Check the top left and top right for a tile collision.
      def isWalkableTop(translateX: DoubleProperty, translateY: DoubleProperty): Boolean =
      {
        // Offset the translation value by 32 because it originally begins at 0.
        if((tileMap.getTileAtPixel(translateX.toInt + 32,
                                   translateY.toInt + 32 - 1).isWalkable) && 
           (tileMap.getTileAtPixel(translateX.toInt + 32 + 31,
                                   translateY.toInt + 32 - 1).isWalkable))
        {
          return true
        }
        else
        {
          return false
        }
      }

      // Check the bottom left and bottom right for a tile collision.
      def isWalkableBottom(translateX: DoubleProperty, translateY: DoubleProperty): Boolean =
      {
        if((tileMap.getTileAtPixel(translateX.toInt + 32,
                                   translateY.toInt + 32 + 31 + 1).isWalkable) && 
           (tileMap.getTileAtPixel(translateX.toInt + 32 + 31,
                                   translateY.toInt + 32 + 31 + 1).isWalkable))
        {
          return true
        }
        else
        {
          return false
        }
      }

      // Check the top left and bottom left for a tile collision.
      def isWalkableLeft(translateX: DoubleProperty, translateY: DoubleProperty): Boolean =
      {
        if((tileMap.getTileAtPixel(translateX.toInt + 32 - 1,
                                   translateY.toInt + 32).isWalkable) &&
           (tileMap.getTileAtPixel(translateX.toInt + 32 - 1,
                                   translateY.toInt + 32 + 31).isWalkable))
        {
          return true
        }
        else
        {
          return false
        }
      }

      // Check the top right and bottom right for a tile collision.
      def isWalkableRight(translateX: DoubleProperty, translateY: DoubleProperty): Boolean =
      {
        if((tileMap.getTileAtPixel(translateX.toInt + 32 + 31 + 1,
                                   translateY.toInt + 32).isWalkable) &&
           (tileMap.getTileAtPixel(translateX.toInt + 32 + 31 + 1,
                                   translateY.toInt + 32 + 31).isWalkable))
        {
          return true
        }
        else
        {
          return false
        }
      }
	  
	  // Check if the hero has moved on the warp tile.
      def onWarpTile(translateX: DoubleProperty, translateY: DoubleProperty): Boolean =
      {
        if((mapManager.getExitLocationX() == math.floor((translateX.toInt + 32 + 16) / 32)) &&
           (mapManager.getExitLocationY() == math.floor((translateY.toInt + 32 + 16) / 32)))
        {
          return true
        }
        else
        {
          return false
        }
      }

      // Create a new random tileMap, and reset the game.
     // Create a new random tileMap, and reset the game.
      def newTileMap()
      {
		myT1.stop()
		myT2.stop()
		myT3.stop()
		myT4.stop()
		myT5.stop()
		myT6.stop()
        // Reset the hero's location.
        heroX() = 0
        heroY() = 0
		bcount = 0

        // Reset the hero's health.
        MyHero.health = 100

        // Create a new random tileMap.
        var tileMap = mapManager.createRandomTileMap()
        var flatTileMap = tileMap.getFlattenedImageArray()

        // Clear the image buffer.
        imageBuffer.clear()
        for(m <- 0 to 5) {gameComponents.children -= enemyArray(m)}
		for(j <- 0 to 9) { gameComponents.children -= bullets(j)}
        // Add the tileMap in the imageBuffer.
        for(i <- 0 to (flatTileMap.length - 1))
        {
          imageBuffer += flatTileMap(i)
        }

        // Add the edge boundaries into the imageBuffer.
        imageBuffer += topWall
        imageBuffer += bottomWall
        imageBuffer += leftWall
        imageBuffer += rightWall

        // Add the hero to the imageBuffer.
        imageBuffer += hero
			
        generateEnemies()
	
        //add enemies to imageBuffer
        for(j <- 0 to 5)
        {
          gameComponents.children += enemyArray(j)
		}
		
		//reset bullets
		for(m <- 0 to 9){
	
			bullets(m) = createBullet()
	
				}
		
		//add bullets to image buffer
		for(j <- 0 to 9)
        {
          gameComponents.children += bullets(j)
		}
		enemySpeed +=5
      }
	  
	  // Hero enemy collion 
	  def heroEnemyCollision(): Boolean =
	  {
	  var collision = false
	  var a = 0
	  for(a <- 0 to 5)
		if(hero.boundsInParent().intersects(enemyArray(a).boundsInParent()) == true) 
			{
			MyHero.health -= enemyDamage
			gameComponents.children -= enemyArray(a)
			enemyArray(a) = new ImageView {}
			collision = true
				}
				if(MyHero.health == 0 || MyHero.health < 0)
				{
					for(m <- 0 to 5){ enemyArray(m) = new ImageView {}}
					stateManager.pushState(new GameOverState(stage, stateManager))
					stage.scene = stateManager.peekState()
				}
						
			return collision
		
	  }
	  
	  
	  // bullets collision
	  def bulletCollision(currBullet: Int)
	  {
	  
		var bulletCollision = false
			
		for(m <- 0 to 5)			
			if(bullets(currBullet).boundsInParent().intersects(enemyArray(m).boundsInParent()) == true) 
			{
			var n = m
			gameComponents.children -= enemyArray(n)
			enemyArray(n) = new ImageView {}
				n =0
				}
				
		
	  }
	  
	def removebullet(currBullet: Int)
	{
		gameComponents.children -= bullets(currBullet)
		bullets(currBullet) = new Rectangle{}
	}
	  
	  //Enemy Movement
	  def enemyMove(i:Int)
	  {
		var xPosition = enemyArray(i).x.value + enemyArray(i).layoutX.value
		var yPosition = enemyArray(i).y.value + enemyArray(i).layoutY.value
		if(yPosition < heroY()){	enemyArray(i).y() += enemySpeed}
		else if(xPosition > heroX()){	enemyArray(i).x() -= enemySpeed}
		else if(yPosition > heroY()){	enemyArray(i).y() -= enemySpeed}
		else if(xPosition < heroX()){	enemyArray(i).x() += enemySpeed}
	  }
	  var fakeEnemy = new Rectangle{}
	  //enemies timelines
	   var myT1 = new Timeline {
		cycleCount = 1
		autoReverse = false
		keyFrames ={	at (1 s) {Set(fakeEnemy.x -> -50.0 )}
    }
  }
		 var myT2 = new Timeline {
		cycleCount = 1
		autoReverse = false
		keyFrames ={	at (1 s) {Set(fakeEnemy.x -> -50.0 )}
    }
  }
	 var myT3 = new Timeline {
		cycleCount = 1
		autoReverse = false
		keyFrames ={	at (1 ms) {Set(fakeEnemy.x -> -50.0 )}
    }
  }
   var myT4 = new Timeline {
		cycleCount = 1
		autoReverse = false
		keyFrames ={	at (1 s) {Set(fakeEnemy.x -> -50.0 )}
    }
  }
   var myT5 = new Timeline {
		cycleCount = 1
		autoReverse = false
		keyFrames ={	at (1 ms) {Set(fakeEnemy.x -> -50.0 )}
    }
  }
   var myT6 = new Timeline {
		cycleCount = 1
		autoReverse = false
		keyFrames ={	at (1 s) {Set(fakeEnemy.x -> -50.0 )}
    }
  }
	 def enemiesMovement()
	 {
		
		myT1.play()
		 myT1.onFinished = {	event: ActionEvent =>	
		 enemyMove(0)
		 myT2.play()}
		 myT2.onFinished = {	event: ActionEvent =>	
		 enemyMove(1) 
		 myT3.play()}
		 myT3.onFinished = {	event: ActionEvent =>	
		 enemyMove(2)
		 myT4.play()}
		 myT4.onFinished = {	event: ActionEvent =>	
		 enemyMove(3) 
		 myT5.play()}
		 myT5.onFinished = {	event: ActionEvent =>	
		 enemyMove(4)
		 myT6.play()}
		 myT6.onFinished = {	event: ActionEvent =>	enemyMove(5)}
	 }
	

   var gameComponents: Group = new Group
   {
     children = imageBuffer

     if(onWarpTile(hero.translateX, hero.translateY))
     {
       newTileMap()
     }

       onKeyPressed = (k: KeyEvent) => k.code match
     {
      
        case KeyCode.UP if(isWalkableTop(hero.translateX, hero.translateY)) =>
		enemiesMovement()
	    if (heroEnemyCollision())
		{
			//stateManager.pushState(new GameOverState(stage, stateManager))
			//stage.scene = stateManager.peekState()
		}
		
		 if (currImage == 1){
		 hero.image = new Image("/scalafx/images/up2.png")
		 currImage = 2
		 } 
		 else{
		 hero.image = new Image("/scalafx/images/up1.png")
		 currImage = 1
		 }
         heroY() = heroY.value - MyHero.speed
	
        case KeyCode.DOWN if(isWalkableBottom(hero.translateX, hero.translateY)) =>
		enemiesMovement()
          if(onWarpTile(hero.translateX, hero.translateY))
          {
            newTileMap()
          }
	    if (heroEnemyCollision())
		{
			//stateManager.pushState(new GameOverState(stage, stateManager))
			//stage.scene = stateManager.peekState()
		}
		
	      if (currImage == 1){
		 hero.image = new Image("/scalafx/images/down2.png")
		 currImage = 2
		 } 
		 else{
		 hero.image = new Image("/scalafx/images/down1.png")
		 currImage = 1
		 }
         heroY() = heroY.value + MyHero.speed
	
        case KeyCode.LEFT if(isWalkableLeft(hero.translateX, hero.translateY)) =>
		enemiesMovement()
          if(onWarpTile(hero.translateX, hero.translateY))
          {
            newTileMap()
          }
	    if (heroEnemyCollision())
		{
			//stateManager.pushState(new GameOverState(stage, stateManager))
			//stage.scene = stateManager.peekState()
		}
		
	      if (currImage == 1){
		 hero.image = new Image("/scalafx/images/left2.png")
		 currImage = 2
		 } 
		 else{
		 hero.image = new Image("/scalafx/images/left1.png")
		 currImage = 1
		 }
         heroX() = heroX.value - MyHero.speed
		
        case KeyCode.RIGHT if(isWalkableRight(hero.translateX, hero.translateY)) =>
		enemiesMovement()
          if(onWarpTile(hero.translateX, hero.translateY))
          {
            newTileMap()
          }
	    if (heroEnemyCollision())
		{
			//stateManager.pushState(new GameOverState(stage, stateManager))
			//stage.scene = stateManager.peekState()
		}
	   
	      if (currImage == 1){
		 hero.image = new Image("/scalafx/images/right2.png")
		 currImage = 2
		 } 
		 else{
		 hero.image = new Image("/scalafx/images/right1.png")
		 currImage = 1
		 }
         heroX() = heroX.value + MyHero.speed
		
		
       case KeyCode.ENTER =>
         stateManager.pushState(new PauseState(stage, stateManager))
         stage.scene = stateManager.peekState()
       case KeyCode.ESCAPE =>
         stateManager.popState()
         stage.scene = stateManager.peekState()
	   case KeyCode.S =>
	   var d = bcount
	   val kf1 = KeyFrame(10 s, onFinished = {
    event: ActionEvent =>
      bulletCollision(bcount)
	  at (1/8 s) {bullets(bcount).x -> 100.0 }
	  at (1/4 s) {bullets(bcount).x -> 100.0 }
	  at (1 s) {bullets(bcount).x -> 300.0 }
	  })
	  val kf2 = KeyFrame(1 ms, onFinished = {
    event: ActionEvent =>
      bulletCollision(bcount)
	  //bullets(bcount).x() 
	  })
	  
		if(bcount < 10)
		{
	var timeline1 = new Timeline {
		cycleCount = 1
		autoReverse = false
		keyFrames ={
		at (1 s) {bullets(bcount).x -> 50.0 }
		//at (1/4 s) {bullets(bcount).x -> 100.0 }
		//at (1 s) {bullets(bcount).x -> 150.0 }
    }
  }
  var timeline2 = new Timeline {
		cycleCount = 1
		autoReverse = false
		keyFrames ={
		at (1 s) {bullets(bcount).x -> 100.0 }
		//at (1/4 s) {bullets(bcount).x -> 100.0 }
		//at (1 s) {bullets(bcount).x -> 150.0 }
    }
  }
  var timeline3 = new Timeline {
		cycleCount = 1
		autoReverse = false
		keyFrames ={
		at (1 s) {bullets(bcount).x -> 150.0 }
		//at (1/4 s) {bullets(bcount).x -> 100.0 }
		//at (1 s) {bullets(bcount).x -> 150.0 }
    }
  }
         heroShoot()
		 timeline1.play()
		 timeline1.onFinished = {
		event: ActionEvent =>
		bulletCollision(bcount)
		//removebullet(d)
		//bcount +=1
	  //bullets(bcount).x() 
	  }
	  timeline2.play()
	  timeline2.onFinished = {
		event: ActionEvent =>
		bulletCollision(bcount)
		//removebullet(d)
		//bcount +=1
	  //bullets(bcount).x() 
	  }
	  timeline3.play()
	  timeline3.onFinished = {
		event: ActionEvent =>
		bulletCollision(bcount)
		removebullet(bcount)
		bcount +=1 
	  }
		enemiesMovement()
		 }
		 //bullet.visible = false
       case _ =>  
     }
   }
   
   // Hero object initialization
   def heroInitialize()
   {
    MyHero.img = hero
	MyHero.health = 100
	MyHero.damage = 10
	MyHero.speed = 8
	MyHero.positionX = 32
	MyHero.positionY = 32
	}
	
	def enemyInitialize()
   {
   var k = 0
   for(k <- 0 to 5)
   {
	MyEnemies(k).damage = 50
	}
	}
	/**
	//play sound
	def playSound()
	{
		val media = new Media("/scalafx/audio/rainbow.mp3")
		val player = new MediaPlayer(media)
		
			while(1==1)
			{
			player.play()
			}
	}
	*/

	
	

//initialize hero
   def initialize()
   {
     heroX() = 0
     heroY() = 0
     gameComponents.requestFocus()
   }

   // Scene assignments
   root = new Group
   {
     fill = LinearGradient(
       startX = 0.0,
       startY = 0.0,
       endX = 0.0,
       endY = 1.0,
       proportional = true,
       cycleMethod = CycleMethod.NoCycle,
       stops = List(Stop(0.0, Color.Black), Stop(0.0, Color.White))
     )
     content = gameComponents
   }
   
  

   initialize()
   heroInitialize()
  // playSound()
   //enemyInitialize()
   //generateEnemies()
}

class InstructionState(stage: JFXApp.PrimaryStage, stateManager: StateManager) extends AppState
{
  fill = Color.Black

  onKeyPressed = (k: KeyEvent) => k.code match
  {
    case KeyCode.ENTER =>
      stateManager.popState()
      stage.scene = stateManager.peekState()
    case KeyCode.ESCAPE =>
      stateManager.popState()
      stage.scene = stateManager.peekState()
    case _ =>
  }

  root = new BorderPane
  {
    center = new StackPane
    {
      content = List(
        new Rectangle
        {
          x = 0
          y = 0
          width = 768
          height = 544
          fill = Color.Blue
        },
        new VBox
        {
          children = List(
            new Label
            {
              text = "Move Hero: Arrow Keys"
              font = new Font("Arial Black", 20)
              textFill = Color.web("#FFFFFF")
            },
            new Label
            {
              text = "Pause Game: Enter"
              font = new Font("Arial Black", 20)
              textFill = Color.web("#FFFFFF")
            },
            new Label
            {
              text = "Back to Main Menu: Escape"
              font = new Font("Arial Black", 20)
              textFill = Color.web("#FFFFFF")
            },
			new Label
            {
              text = "Shoot: S"
              font = new Font("Arial Black", 20)
              textFill = Color.web("#FFFFFF")
            },
			new Label
            {
              text = "Goal: Avoid enemies and get to exit(Bottom right corner)"
              font = new Font("Arial Black", 20)
              textFill = Color.web("#FFFFFF")
            }
          )
        }
      )
    }
  }
}

class PauseState(stage: JFXApp.PrimaryStage, stateManager: StateManager) extends AppState
{
  fill = Color.Blue

  onKeyPressed = (k: KeyEvent) => k.code match
  {
    case KeyCode.ENTER =>
      stateManager.popState()
      stage.scene = stateManager.peekState()
    case KeyCode.ESCAPE =>
      stateManager.popState()
      stage.scene = stateManager.peekState()
    case _ =>
  }

  root = new BorderPane
  {
    center = new StackPane
    {
      content = List(
        new Rectangle
        {
          x = 0
          y = 0
          width = 768
          height = 544
          fill = Color.Black
        },
        new Label
        {
          text = "Paused"
          font = new Font("Arial Black", 40)
          textFill = Color.web("#FFFFFF")
        }
      )
    }
  }
}


class GameOverState(stage: JFXApp.PrimaryStage, stateManager: StateManager) extends AppState
{
  fill = Color.Black
  
   onKeyPressed = (k: KeyEvent) => k.code match
  {
    case KeyCode.ENTER =>
      stateManager.popState()
      stage.scene = stateManager.peekState()
    case KeyCode.ESCAPE =>
      stateManager.popState()
      stage.scene = stateManager.peekState()
    case _ =>
  }

  root = new Group
  {
    children = List(
      new ImageView
      {
        image = new Image("/scalafx/images/gameover.png")
        layoutX = 0
        layoutY = 0
		fitHeight = 580
		fitWidth = 780
        preserveRatio = false
        smooth = true
      }
        )
      }
  }

