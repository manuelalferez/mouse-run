package mouserun.mouse;

import javafx.util.Pair;
import mouserun.game.Cheese;
import mouserun.game.Grid;
import mouserun.game.Mouse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class M20A01_GRE extends Mouse {
    private static final int MOTIONLESS = -1;
    private Grid lastGrid;
    private HashMap<Pair<Integer, Integer>, Grid> gridsVisited;
    private Stack<Integer> pileOfMovements;

    private HashMap<Pair<Integer, Integer>, Grid> nodesVisited;
    private ArrayList<Integer> path;
    private boolean pathUsed;

    public M20A01_GRE() {
        super("M20A01_GRE");
        gridsVisited = new HashMap<>();
        pileOfMovements = new Stack<>();
        path = new ArrayList<>();
        pathUsed = false;
    }

    /**
     * @param currentGrid Casilla donde el ratón está
     * @param cheese      El queso a buscar
     * @return Un movimiento
     * @brief //TODO Detallar la estrategia que define la función
     */
    @Override
    public int move(Grid currentGrid, Cheese cheese) {
        Grid cheeseGrid = new Grid(cheese.getX(), cheese.getY());
        if (!path.isEmpty()) {
            int movement = path.get(0);
            path.remove(0);
            return movement;
        } else {
            if (visited(cheeseGrid)) { // Hay que ir hacia el queso
                addVisitedGrid(currentGrid);
                path = getPath(currentGrid, cheeseGrid);
                int movement = path.get(0);
                path.remove(0);
                pathUsed = true;
                return movement;
            } else {
                // Camino hacia casilla sin explorar
                // Volver a la casilla donde había empezado a buscar el queso, para empezar a explorar de nuevo
                if (pathUsed) {
                    path = goToUntappedGrid(currentGrid);
                    pathUsed = false;
                    int movement = path.get(0);
                    path.remove(0);
                    return movement;
                } else { // Exploración
                    int movement = getMovement(currentGrid);
                    if (movement != MOTIONLESS) {
                        System.out.println("Explorando");
                        lastGrid = currentGrid;
                        pileOfMovements.push(getContraryMovement(movement));
                        addVisitedGrid(currentGrid);
                        return movement;
                    } else {
                        lastGrid = currentGrid;
                        addVisitedGrid(currentGrid);
                        if (!pileOfMovements.empty()) {
                            System.out.println("Marcha atrás");
                            return pileOfMovements.pop();
                        } else { // Salida
                            int move = getOut(currentGrid);
                            pileOfMovements.push(getContraryMovement(move));
                            return move;
                        }
                    }
                }
            }
        }
    }

    /**
     * @param currentGrid     Posición donde se encuentra el ratón
     * @param destinationGrid Posición de la casilla destino (normalmente la del queso)
     * @return Una pila de movimiento hasta el queso
     * @brief Analizar los posibles caminos hasta llegar al queso
     */
    private ArrayList<Integer> getPath(Grid currentGrid, Grid destinationGrid) {
        nodesVisited = new HashMap<>();
        Stack<Grid> parentsGrid = new Stack<>();
        ArrayList<Integer> buildingPath = new ArrayList<>();
        parentsGrid.push(currentGrid);
        addVisitedNode(parentsGrid.peek());
        Grid nextGrid;

        while (notEqual(parentsGrid.peek(), destinationGrid)) {
            if (notEqual(parentsGrid.peek(), currentGrid))
                addVisitedNode(parentsGrid.peek());
            if (parentsGrid.peek().canGoUp() && nodeNotVisited(getDestinationGrid(parentsGrid.peek(), UP)) &&
                    visited(getDestinationGrid(parentsGrid.peek(), UP))) {
                nextGrid = getEndGrid(parentsGrid.peek(), UP);
                parentsGrid.push(nextGrid);
                buildingPath.add(UP);
            } else if (parentsGrid.peek().canGoRight() && nodeNotVisited(getDestinationGrid(parentsGrid.peek(), RIGHT)) &&
                    visited(getDestinationGrid(parentsGrid.peek(), RIGHT))) {
                nextGrid = getEndGrid(parentsGrid.peek(), RIGHT);
                parentsGrid.push(nextGrid);
                buildingPath.add(RIGHT);
            } else if (parentsGrid.peek().canGoDown() && nodeNotVisited(getDestinationGrid(parentsGrid.peek(), DOWN)) &&
                    visited(getDestinationGrid(parentsGrid.peek(), DOWN))) {
                nextGrid = getEndGrid(parentsGrid.peek(), DOWN);
                parentsGrid.push(nextGrid);
                buildingPath.add(DOWN);
            } else if (parentsGrid.peek().canGoLeft() && nodeNotVisited(getDestinationGrid(parentsGrid.peek(), LEFT)) &&
                    visited(getDestinationGrid(parentsGrid.peek(), LEFT))) {
                nextGrid = getEndGrid(parentsGrid.peek(), LEFT);
                parentsGrid.push(nextGrid);
                buildingPath.add(LEFT);
            } else {
                parentsGrid.pop();
                buildingPath.remove(buildingPath.size() - 1);
            }
        }

        System.out.println("Queso encontrado [getPath]");
        return buildingPath;
    }

    private int getManhattan(Grid currentGrid, Grid cheeseGrid) {
        return Math.abs(currentGrid.getX() - cheeseGrid.getX()) + Math.abs(currentGrid.getY() - cheeseGrid.getY());
    }

    /**
     * @param currentGrid La casilla en la que nos encontramos
     * @return Un camino hacia una casilla sin explorar
     * @brief Encontrar una casilla aún sin explorar
     */
    private ArrayList<Integer> goToUntappedGrid(Grid currentGrid) {
        nodesVisited = new HashMap<>();
        Stack<Grid> parentsGrid = new Stack<>();
        ArrayList<Integer> buildingPath = new ArrayList<>();
        parentsGrid.push(currentGrid);
        addVisitedNode(parentsGrid.peek());
        Grid nextGrid;

        while (visited(parentsGrid.peek())) {
            if (notEqual(parentsGrid.peek(), currentGrid))
                addVisitedNode(parentsGrid.peek());
            if (parentsGrid.peek().canGoUp() && nodeNotVisited(getDestinationGrid(parentsGrid.peek(), UP))) {
                buildingPath.add(UP);
                if (visited(getDestinationGrid(parentsGrid.peek(), UP))) {
                    nextGrid = getEndGrid(parentsGrid.peek(), UP);
                    parentsGrid.push(nextGrid);
                } else {
                    break;
                }
            } else if (parentsGrid.peek().canGoRight() && nodeNotVisited(getDestinationGrid(parentsGrid.peek(), RIGHT))) {
                buildingPath.add(RIGHT);
                if (visited(getDestinationGrid(parentsGrid.peek(), RIGHT))) {
                    nextGrid = getEndGrid(parentsGrid.peek(), RIGHT);
                    parentsGrid.push(nextGrid);
                } else {
                    break;
                }
            } else if (parentsGrid.peek().canGoDown() && nodeNotVisited(getDestinationGrid(parentsGrid.peek(), DOWN))) {
                buildingPath.add(DOWN);
                if (visited(getDestinationGrid(parentsGrid.peek(), DOWN))) {
                    nextGrid = getEndGrid(parentsGrid.peek(), DOWN);
                    parentsGrid.push(nextGrid);
                } else {
                    break;
                }
            } else if (parentsGrid.peek().canGoLeft() && nodeNotVisited(getDestinationGrid(parentsGrid.peek(), LEFT))) {
                buildingPath.add(LEFT);
                if (visited(getDestinationGrid(parentsGrid.peek(), LEFT))) {
                    nextGrid = getEndGrid(parentsGrid.peek(), LEFT);
                    parentsGrid.push(nextGrid);
                } else {
                    break;
                }
            } else {
                parentsGrid.pop();
                buildingPath.remove(buildingPath.size() - 1);
            }
        }

        System.out.println("Casilla sin explorar encontrada [goToUntappedGrid]");
        return buildingPath;
    }

    public boolean notEqual(Grid first, Grid second) {
        return first.getX() != second.getX() || first.getY() != second.getY();
    }

    /**
     * @param node Nodo que se está procesando
     * @return True si el nodo no ha sido visitado
     * @brief Informa si el nodo está en nodosVisitados
     */
    public boolean nodeNotVisited(Grid node) {
        Pair<Integer, Integer> pair = new Pair<>(node.getX(), node.getY());
        return !nodesVisited.containsKey(pair);
    }

    /**
     * @param first Primera casilla
     * @param end   Segunda casilla
     * @return Un movimiento
     * @brief Dadas dos casillas devuelve el movimiento necesario para llegar de 'first' a 'end'
     */
    private int getMovementToReach(Grid first, Grid end) {
        if (end == getDestinationGrid(first, UP))
            return UP;
        if (end == getDestinationGrid(first, DOWN))
            return DOWN;
        if (end == getDestinationGrid(first, RIGHT))
            return RIGHT;
        return LEFT;
    }

    /**
     * @param currentGrid Casilla donde se encuentra el ratón
     * @return Un movimiento válido si existe, o ningún movimiento en otro caso
     * @brief Movimiento que el ratón puede realizar de acuerdo a las siguientes restricciones:
     * - La casilla a la que el ratón se mueve no esta visitada
     * - La casilla a la que el ratón se mueve es distinta de la que viene
     * - Que el ratón pueda realizar el movimiento, es decir, no existan muros en la dirección a la que se mueve
     */
    private int getMovement(Grid currentGrid) {
        int result = MOTIONLESS;
        if (currentGrid.canGoUp() && isNotLastGrid(currentGrid, this.lastGrid, UP) &&
                !visited(getDestinationGrid(currentGrid, UP)))
            result = Mouse.UP;
        else if (currentGrid.canGoRight() && isNotLastGrid(currentGrid, this.lastGrid, RIGHT) &&
                !visited(getDestinationGrid(currentGrid, RIGHT)))
            result = Mouse.RIGHT;
        else if (currentGrid.canGoDown() && isNotLastGrid(currentGrid, this.lastGrid, DOWN) &&
                !visited(getDestinationGrid(currentGrid, DOWN)))
            result = Mouse.DOWN;
        else if (currentGrid.canGoLeft() && isNotLastGrid(currentGrid, this.lastGrid, LEFT) &&
                !visited(getDestinationGrid(currentGrid, LEFT)))
            result = Mouse.LEFT;

        return result;
    }

    /**
     * @param currentGrid Casilla donde se encuentra el ratón
     * @return Un movimiento
     * @brief Se proporciona un movimiento, cuando tenemos la pila vacía, que inicia la salida del ratón
     */
    private int getOut(Grid currentGrid) {
        if (currentGrid.canGoUp() && isNotLastGrid(currentGrid, this.lastGrid, UP))
            return Mouse.UP;
        else if (currentGrid.canGoLeft() && isNotLastGrid(currentGrid, this.lastGrid, LEFT))
            return Mouse.LEFT;
        else if (currentGrid.canGoDown() && isNotLastGrid(currentGrid, this.lastGrid, DOWN))
            return Mouse.DOWN;
        else
            return Mouse.RIGHT;
    }

    public boolean isNotLastGrid(Grid currentGrid, Grid lastGrid, int direction) {
        if (lastGrid == null)
            return false;

        Grid destinationGrid = getDestinationGrid(currentGrid, direction);

        return (lastGrid.getX() != destinationGrid.getX() || lastGrid.getY() != destinationGrid.getY());
    }

    void addVisitedGrid(Grid currentGrid) {
        boolean isGrid = gridsVisited.containsKey(new Pair<>(currentGrid.getX(), currentGrid.getY()));
        if (!isGrid) {
            gridsVisited.put(new Pair<>(currentGrid.getX(), currentGrid.getY()), currentGrid);
            incExploredGrids();
        }
    }

    void addVisitedNode(Grid node) {
        boolean isGrid = nodesVisited.containsKey(new Pair<>(node.getX(), node.getY()));
        if (!isGrid) {
            nodesVisited.put(new Pair<>(node.getX(), node.getY()), node);
        }
    }

    /**
     * @param movement Movimiento a contrariar
     * @return Un movimiento
     * @brief Dado un movimiento, calcula el movimiento que es contrario, es decir:
     * - UP -> DOWN
     * - LEFT -> RIGHT
     * - DOWN -> UP
     * - RIGHT -> LEFT
     */
    int getContraryMovement(int movement) {
        if (movement % 2 == 0)
            movement = movement - 1;
        else
            movement = movement + 1;
        return movement;
    }

    /**
     * @param grid Casilla procesada
     * @return True si la casilla es una celda visitada
     * @brief Procesa si la casilla está en celdasVisitadas
     */
    public boolean visited(Grid grid) {
        Pair<Integer, Integer> pair = new Pair<>(grid.getX(), grid.getY());
        return gridsVisited.containsKey(pair);
    }

    /**
     * @param currentGrid Casilla dónde nos encontramos
     * @param direction   Dirección a aplicar
     * @return Casilla destino
     * @brief Calcula la casilla destino después de aplicar una dirección sobre una casilla
     */
    public Grid getDestinationGrid(Grid currentGrid, int direction) {
        int x = currentGrid.getX();
        int y = currentGrid.getY();

        switch (direction) {
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


    public Grid getEndGrid(Grid first, int direction) {
        int x = first.getX();
        int y = first.getY();

        switch (direction) {
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
        return gridsVisited.get(new Pair<>(x, y));
    }

    /**
     * @brief Método que se llama cuando el ratón pisa una bomba
     */
    @Override
    public void respawned() {
        pileOfMovements.clear();
    }

    /**
     * @brief Método que se llama cuando aparece un nuevo queso
     */
    @Override
    public void newCheese() {
        path.clear();
    }
}
