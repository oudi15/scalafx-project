package mapManager
{
  import scala.util.{Random}
  import scalafx.scene.image.{Image, ImageView}

  package tile
  {
    class Tile
    {
      var id = ""
      var imageView = new ImageView {}
      var isWalkable = false

      var pixelLocationX = 0
      var pixelLocationY = 0
      var matrixLocationX = 0
      var matrixLocationY = 0

      def setMatrixLocation(x: Int, y: Int)
      {
        matrixLocationX = x
        matrixLocationY = y

        // Calculate the pixel locations.
        pixelLocationX = matrixLocationX * 32
        pixelLocationY = matrixLocationY * 32

        // Set the image location.
        imageView.layoutX = pixelLocationX
        imageView.layoutY = pixelLocationY
      }
    }

    // Tiles contains all the tile information mapped to id's.
    class Tiles
    {
      val imageLocations = Map(("DFT1", "/scalafx/images/tiles/DFT1.png"),
                               ("DFT2", "/scalafx/images/tiles/DFT2.png"),
                               ("DWT1", "/scalafx/images/tiles/DWT1.png"),
                               ("WT", "/scalafx/images/tiles/WT.png"))
      val isWalkables = Map(("DFT1", true),
                            ("DFT2", true),
                            ("DWT1", false),
                            ("WT", true))
    }

    // The basic tileMap.
    class TileMap
    {
      val mapLength = 24
      val mapHeight = 17

      var tileMap = Array.ofDim[Tile](mapLength, mapHeight)
      val tiles = new Tiles {}

      // Initialize the array.
      for(i <- 0 to (mapLength - 1))
      {
        for(j <- 0 to (mapHeight - 1))
        {
          tileMap(i)(j) = new Tile {}
        }
      }

      def getFlattenedImageArray(): Array[ImageView] =
      {
        var flattenedArray = new Array[ImageView](mapLength * mapHeight)
        var arrayIndex = 0

        for(i <- 0 to mapLength - 1)
        {
          for(j <- 0 to mapHeight - 1)
          {
            flattenedArray(arrayIndex) = tileMap(i)(j).imageView
            arrayIndex = arrayIndex + 1
          }
        }
        return flattenedArray
      }

      def getTileAtPixel(x: Int, y: Int): Tile =
      {
        return tileMap(x / 32)(y / 32)
      }

      def setTileAtMatrix(x: Int, y: Int, tileType: String)
      {
          tileMap(x)(y).id = tileType
          tileMap(x)(y).imageView.image = new Image(tiles.imageLocations(tileType))
          tileMap(x)(y).isWalkable = tiles.isWalkables(tileType)
          tileMap(x)(y).setMatrixLocation(x, y)
      }

      def getTileAtMatrix(x: Int, y: Int): Tile =
      {
        return tileMap(x)(y)
      }

      def getTileMapLength() : Int =
      {
        return mapLength
      }

      def getTileMapHeight() : Int =
      {
        return mapHeight
      }
    }
  }

  class MapManager
  {
    var tileMap = new tile.TileMap {}
    var Tiles = new tile.Tiles {}

    var heroStartX = 1
    var heroStartY = 1
    var exitLocationX = 22
    var exitLocationY = 15

    def createRandomTileMap(): tile.TileMap =
    {
      // Boolean array that contains each wall location.
      var wallArray = Array.ofDim[Boolean](tileMap.getTileMapLength(),
                                           tileMap.getTileMapHeight())

      var randomNumber = Random
      var randomDirection = randomNumber.nextInt(4)
      var randomLocationX = heroStartX
      var randomLocationY = heroStartY
      var sameDirectionCount = 0

      // Initialize the wallArray with wall tiles.
      for(i <- 0 to (tileMap.getTileMapLength() - 1))
      {
        for(j <- 0 to (tileMap.getTileMapHeight() - 1))
        {
          wallArray(i)(j) = true
        }
      }

      // Initialize the heroStart and exitLocation tiles.
      wallArray(heroStartX)(heroStartY) = false
      wallArray(exitLocationX)(exitLocationY) = false

      // Generate a random maze.
      while((randomLocationX != exitLocationX) || (randomLocationY != exitLocationY))
      {
        // Minimize the number of tiles before a turn.
        // Increasing this value would seem to increase the difficulty
        // of the game.
        if(sameDirectionCount < 2)
        {
          sameDirectionCount += 1
        }
        else
        {
          randomDirection = randomNumber.nextInt(4)
          sameDirectionCount = 0
        }

        // Up.
        if(randomDirection == 0)
        {
          // Check if the randomLocationY is within the bounds of the upper
          // wall.
          if((randomLocationY - 1) >= 1)
          {
            // Check if the respective tile is already a floor tile.
            //if(wallArray(randomLocationX)(randomLocationY - 1) != false)
            //{
              // Open a floor tile.
              randomLocationY -= 1
              wallArray(randomLocationX)(randomLocationY) = false
            //}
          }
        }
        // Down.
        else if(randomDirection == 1)
        {
          // Check if the randomLocationY is within the bounds of the lower 
          // wall.
          if((randomLocationY + 1) <= (tileMap.getTileMapHeight() - 2))
          {
            // Check if the respective tile is already a floor tile.
            //if(wallArray(randomLocationX)(randomLocationY + 1) != false)
            //{
              // Open a floor tile.
              randomLocationY += 1
              wallArray(randomLocationX)(randomLocationY) = false
            //}
          }
        }
        // Left.
        else if(randomDirection == 2)
        {
          // Check if the randomLocationX is within the bounds of the left
          // wall.
          if((randomLocationX - 1) >= 1)
          {
            // Check if the respective tile is already a floor tile.
            //if(wallArray(randomLocationX - 1)(randomLocationY) != false)
            //{
              // Open a floor tile.
              randomLocationX -= 1
              wallArray(randomLocationX)(randomLocationY) = false
            //}
           
          }
        }
        // Right.
        else if(randomDirection == 3)
        {
          // Check if the randomLocationX is within the bounds of the right
          // wall.
          if((randomLocationX + 1) <= tileMap.getTileMapLength() - 2)
          {
            // Check if the respective tile is already a floor tile.
            //if(wallArray(randomLocationX + 1)(randomLocationY) != false)
            //{
              // Open a floor tile.
              randomLocationX += 1
              wallArray(randomLocationX)(randomLocationY) = false
            //}
          }
        }
      }

      // Set the tiles according to the wallArray.
      for(i <- 0 to (tileMap.getTileMapLength() - 1))
      {
        for(j <- 0 to (tileMap.getTileMapHeight() - 1))
        {
          // Set the exit tile.
          if((i == exitLocationX) && (j == exitLocationY))
          {
            tileMap.setTileAtMatrix(i, j, "WT")
          }
          // Set the wall tiles.
          else if(wallArray(i)(j) == true)
          {
            tileMap.setTileAtMatrix(i, j, "DWT1")
          }
          // Set the floor tiles.
          else
          {
            tileMap.setTileAtMatrix(i, j, "DFT2")
          }
        }
      }
      return tileMap
    }

    def getExitLocationX(): Int =
    {
      return exitLocationX
    }

    def getExitLocationY(): Int =
    {
      return exitLocationY
    }
  }
}
