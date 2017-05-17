import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameSolver {
    public final static GameSolver INSTANCE = new GameSolver();
    private Map<List<Integer>, Solution> solutionCache = new HashMap<>();

    private GameSolver(){}


    public Solution findPossibleWinningStrategies(List<Integer> board, List<List<Integer>> moves) {
        Solution winsPerPlayer = new Solution();

        findStrategiesRecursive(board, moves, winsPerPlayer);

        return winsPerPlayer;
    }

    public Solution findPossibleWinningStrategies(int[] board, int[][] moves) {
        List<List<Integer>> allPossibleMoves = new ArrayList<>(moves.length);
        for (int i = 0; i < moves.length; i++) {
            List<Integer> move = new ArrayList<>();
            allPossibleMoves.add(move);
            for (int j=0; j<moves[i].length; j++) {
                move.add(moves[i][j]);
            }
        }

        List<Integer> boardAsList = new ArrayList<>();
        for (int i = 0; i < board.length; i++) {
            boardAsList.add(board[i]);
        }

        return findPossibleWinningStrategies(boardAsList, allPossibleMoves);
    }

    private void findStrategiesRecursive(List<Integer> board, List<List<Integer>> possibleMoves, Solution winsPerPlayer) {
        // if no valid moves, determine winner and increment that player's counter (base case of recursion)
        List<List<Integer>> validMoves = getValidMoves(board, possibleMoves);
        if (validMoves.isEmpty()) {
            int winner = findWinner(board);
            winsPerPlayer.incrementWinCount(winner);
        } else {
            // apply all valid moves and continue play with each resulting board
            validMoves.forEach(move -> {
                findStrategiesRecursive(applyMove(board, move), possibleMoves, winsPerPlayer);
            });
        }
    }


    // utility methods

    List<List<Integer>> getValidMoves(List<Integer> board, List<List<Integer>> possibleMoves) {
        List<List<Integer>> result = new ArrayList<>();

        possibleMoves.stream().filter(move -> isValidMove(board, move)).forEach(move -> result.add(move));

        return result;
    }

    int findWinner(List<Integer> board) {
        return (board.get(0) > board.get(board.size()-1) ? 0 : 1);
    }

    boolean isValidMove(List<Integer> board, List<Integer> move) {
        checkBoardAndMoveSizeInSync(board, move);

        for (int i=0; i<board.size(); i++) {
            if (board.get(i) + move.get(i) < 0) {
                return false;
            }
        }

        return true;
    }

    List<Integer> applyMove(List<Integer> board, List<Integer> move) {
        checkBoardAndMoveSizeInSync(board, move);

        List<Integer> result = new ArrayList<>(board.size());
        for (int i=0; i<board.size(); i++) {
            result.add(board.get(i) + move.get(i));
        }

        return result;
    }

    void checkBoardAndMoveSizeInSync(List<Integer> board, List<Integer> move) {
        Preconditions.checkArgument(board.size() == move.size(),
                "Size of the move list (" + move.size()
                        + ") does not match size of the board (" + board.size() +") as required");
    }

    public static class Solution {
        private int player1WinTotal;
        private int player2WinTotal;

        public Solution() {
            player1WinTotal = 0;
            player2WinTotal = 0;
        }

        public Solution(int player1WinTotal, int player2WinTotal) {
            this.player1WinTotal = player1WinTotal;
            this.player2WinTotal = player2WinTotal;
        }

        public void incrementWinCount(int winningPlayer) {
            Preconditions.checkArgument(winningPlayer == 0 || winningPlayer == 1, "Only two players in the game, so expecting either 0 or 1 are argument");
            if (winningPlayer == 0) {
                player1WinTotal += 1;
            } else {
                player2WinTotal += 1;
            }
        }

        public int getPlayer1WinTotal() {
            return player1WinTotal;
        }

        public int getPlayer2WinTotal() {
            return player2WinTotal;
        }

        public void add(Solution solutionToAdd) {
            this.player1WinTotal += solutionToAdd.player1WinTotal;
            this.player2WinTotal += solutionToAdd.player2WinTotal;
        }
    }
}