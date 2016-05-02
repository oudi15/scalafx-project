package EntityManager

import mapManager._
import mapManager.tile._
import scalafx.scene.image.{Image, ImageView}
	
	abstract class Entity
	{
	
	var positionX: Int = 0
	var positionY: Int = 0
	var pixelLocationX = 0
    var pixelLocationY = 0
	var health: Int = 0
	var damage: Int = 0
	var speed: Int = 0
	var img = new ImageView {}
	
	def setLocation(x: Int, y: Int)
	{
		positionX = x
		positionY = y
		
		// Calculate the pixel locations.
        pixelLocationX = positionX * 32
        pixelLocationY = positionY * 32

        // Set the image location.
        img.layoutX = pixelLocationX
        img.layoutY = pixelLocationY
		}
	
	def getDamage(): Int =
	{
		return damage
		}
	
	def setDamage(d: Int)
	{
		damage = d
		}
	
	def getHealth(): Int =
	{
		return health
		}
	
	def setHealth(h: Int)
	{
		health = h
		}
	
	def getSpeed(): Int =
	{
		return speed
		}
	
	def setSpeed(s: Int)
	{
		speed = s
		}
	
	}
	
	class Hero extends Entity
	{
	
		var posx = 320
		var posy = 80
		
}


class Enemy extends Entity
{
	var posx = 0
	var posy = 0
}


		

