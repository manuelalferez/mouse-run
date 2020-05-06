package mouserun.mouse;

import java.util.*;

import javafx.util.Pair;
import mouserun.game.Mouse;
import mouserun.game.Grid;
import mouserun.game.Cheese;

public class M20A01a extends Mouse
{
    private static final int SIN_MOVIMIENTO = -1;
    private Grid lastGrid;
    private HashMap<Pair<Integer, Integer>, Grid> celdasVisitadas;
    private Stack<Integer> pilaMovimientos;

    public M20A01a()
    {
        super("M20A01a");
        celdasVisitadas = new HashMap<>();
        pilaMovimientos = new Stack<>();
    }

    @Override
    public int move(Grid currentGrid, Cheese cheese)
    {
        int movimiento = getMovimiento(currentGrid);
        if (movimiento != SIN_MOVIMIENTO)
        {
            lastGrid = currentGrid;
            addPilaMovimientos(movimiento);
            addCeldaVisitada(currentGrid);
            return movimiento;
        } else
        {
            lastGrid = currentGrid;
            addCeldaVisitada(currentGrid);
            if (!pilaMovimientos.empty())
            {
                return pilaMovimientos.pop();
            } else
            {
                int move = getSalida(currentGrid);
                addPilaMovimientos(move);
                return move;
            }
        }
    }

    private int getMovimiento(Grid currentGrid)
    {
        int result = SIN_MOVIMIENTO;
        if (currentGrid.canGoUp() && !isLastGrid(UP, currentGrid) && !visitada(getDestinationGrid(UP, currentGrid)))
        {
            result = Mouse.UP;
        } else if (currentGrid.canGoRight() && !isLastGrid(RIGHT, currentGrid) && !visitada(getDestinationGrid(RIGHT, currentGrid)))
        {
            result = Mouse.RIGHT;
        } else if (currentGrid.canGoDown() && !isLastGrid(DOWN, currentGrid) && !visitada(getDestinationGrid(DOWN, currentGrid)))
        {
            result = Mouse.DOWN;
        } else if (currentGrid.canGoLeft() && !isLastGrid(LEFT, currentGrid) && !visitada(getDestinationGrid(LEFT, currentGrid)))
        {
            result = Mouse.LEFT;
        }

        return result;
    }

    /**
     * @brief Devuelve un movimiento válido, es decir, cuando tenemos la pila vacía, se proporciona un movimiento
     * que inicie la salida del ratón
     */
    private int getSalida(Grid currentGrid)
    {
        if (currentGrid.canGoUp() && !isLastGrid(UP, currentGrid))
        {
            return Mouse.UP;
        } else if (currentGrid.canGoLeft() && !isLastGrid(LEFT, currentGrid))
        {
            return Mouse.LEFT;
        } else if (currentGrid.canGoDown() && !isLastGrid(DOWN, currentGrid))
        {
            return Mouse.DOWN;
        } else
        {
            return Mouse.RIGHT;
        }
    }

    void addCeldaVisitada(Grid currentGrid)
    {
        boolean esta = celdasVisitadas.containsKey(new Pair<>(currentGrid.getX(), currentGrid.getY()));
        if (!esta)
        {
            celdasVisitadas.put(new Pair<>(currentGrid.getX(), currentGrid.getY()), currentGrid);
            incExploredGrids();
        }
    }

    /**
     * @brief Añade el movimiento contrario, al que se ha realizado, en la pila. Dicho de otro modo, si el movimiento que
     * hemos realizado es hacia arriba (UP), el movimiento que se añade a la pila es hacia abajo (DOWN), de este modo
     * conseguimos salir en situaciones tales como callejones sin salida o nodos con todas las aristas salientes visitadas.
     */
    void addPilaMovimientos(int mov)
    {
        if (mov % 2 == 0)
        {
            mov = mov - 1;
        } else
        {
            mov = mov + 1;
        }
        pilaMovimientos.push(mov);
    }

    /**
     * @brief Método que se llama cuando aparece un nuevo queso
     */
    @Override
    public void newCheese()
    {

    }

    /**
     * @brief Método que se llama cuando el ratón pisa una bomba
     */
    @Override
    public void respawned()
    {
        pilaMovimientos.clear();
    }

    /**
     * @brief Método para evaluar que no nos movamos a la misma celda anterior
     */
    public boolean isLastGrid(int direction, Grid currentGrid)
    {
        if (lastGrid == null)
        {
            return true;
        }

        Grid destinationGrid = getDestinationGrid(direction, currentGrid);

        return (lastGrid.getX() == destinationGrid.getX() && lastGrid.getY() == destinationGrid.getY());
    }

    /**
     * @brief Método que, dada una casilla, nos devuelve la casilla destino después de aplicar una dirección
     */
    public Grid getDestinationGrid(int direction, Grid currentGrid)
    {
        int x = currentGrid.getX();
        int y = currentGrid.getY();

        switch (direction)
        {
            case Mouse.UP:
                y += 1;
                break;

            case Mouse.DOWN:
                y -= 1;
                break;

            case Mouse.LEFT:
                x -= 1;
                break;

            case Mouse.RIGHT:
                x += 1;
                break;
        }
        return new Grid(x, y);
    }

    /**
     * @brief Método que devuelve si de una casilla dada, está contenida en el mapa de celdasVisitadas
     */
    public boolean visitada(Grid casilla)
    {
        Pair par = new Pair(casilla.getX(), casilla.getY());
        return celdasVisitadas.containsKey(par);
    }
}
