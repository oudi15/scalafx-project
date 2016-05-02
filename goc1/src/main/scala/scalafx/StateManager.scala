package stateManager
{
  import scala.collection.mutable.Stack
  import scalafx.scene.Scene
  import scalafx.scene.layout.{BorderPane}

  package appState
  {
    abstract class AppState extends Scene
    {
      var borderPane = new BorderPane {}
    }
  }

  class StateManager
  {
    var stateStack = new Stack[appState.AppState]

    def pushState(state: appState.AppState) : Unit = 
    {
      stateStack.push(state)
    }

    def popState() : Unit = 
    {
      stateStack.pop
    }

    def peekState() : appState.AppState =
    {
      return stateStack.top
    }
  }
}
