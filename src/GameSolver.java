import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameSolver {
    public final static GameSolver INSTANCE = new GameSolver();

    private GameSolver(){}

    public Solution findWinCountsForBoardAndPossibleMoves(List<Integer> board, List<List<Integer>> possibleMoves) {
        return findCountsRecursive(board, possibleMoves, new HashMap<>());
    }

    public Solution findCountsRecursive(List<Integer> board, List<List<Integer>> possibleMoves, Map<List<Integer>, Solution> solutionCache) {
        // first try to retrieve from cache since we see certain boards multiple times
        if (solutionCache.containsKey(board)) {
            return solutionCache.get(board);
        }

        // if no valid moves, determine winner and increment that player's counter (base case of recursion)
        List<List<Integer>> validMoves = getValidMoves(board, possibleMoves);
        Solution solutionToReturn = new Solution();
        if (validMoves.isEmpty()) {
            solutionToReturn.incrementWinCount(findWinner(board));
        } else {
            // apply all valid moves and continue play with each resulting board
            validMoves.forEach(move -> {
                solutionToReturn.add(findCountsRecursive(applyMove(board, move), possibleMoves, solutionCache));
            });
        }

        // store solution for this board in cache to make things more efficient since many
        // boards are seen multiple times
        solutionCache.put(board, solutionToReturn);
        return solutionToReturn;
    }

    public Solution findWinCountsForBoardAndPossibleMoves(int[] board, int[][] moves) {
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

        return findWinCountsForBoardAndPossibleMoves(boardAsList, allPossibleMoves);
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

    private String boardToString(List<Integer> board) {
        StringBuilder sb = new StringBuilder().append("[");
        board.forEach(i -> sb.append(i).append(","));
        sb.deleteCharAt(sb.length()-1).append("]");

        return sb.toString();
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