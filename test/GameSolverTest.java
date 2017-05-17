import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.*;
import static org.hamcrest.core.IsCollectionContaining.*;

public class GameSolverTest {
    @Test
    public void findWinnerReturnsPlayer1WhenPlayer1HasMoreStonesAtEndTest() throws Exception {
        List<Integer> player1WinsBoard = Lists.newArrayList(2,0,0,1);
        assertEquals(0, GameSolver.INSTANCE.findWinner(player1WinsBoard));
    }

    @Test
    public void findWinnerReturnsPlayer2WhenPlayer2HasMoreStonesAtEndTest() throws Exception {
        List<Integer> player2WinsBoard = Lists.newArrayList(1,0,0,2);
        assertEquals(1, GameSolver.INSTANCE.findWinner(player2WinsBoard));
    }

    @Test
    public void findWinnerReturnsPlayer2WhenPlayersHaveEqualNumberOfStonesTest() throws Exception {
        List<Integer> playersEndWithEqualStonesBoard = Lists.newArrayList(2,0,0,2);
        assertEquals(1, GameSolver.INSTANCE.findWinner(playersEndWithEqualStonesBoard));
    }

    @Test
    public void findWinnerIgnoresCountsForNonPlayerStonesTest() throws Exception {
        List<Integer> player1WinsBoard = Lists.newArrayList(2,4,5,1);
        assertEquals(0, GameSolver.INSTANCE.findWinner(player1WinsBoard));


        List<Integer> player2WinsBoard = Lists.newArrayList(1,5,4,2);
        assertEquals(1, GameSolver.INSTANCE.findWinner(player2WinsBoard));

        List<Integer> playersEndWithEqualStonesBoard = Lists.newArrayList(2,3,6,2);
        assertEquals(1, GameSolver.INSTANCE.findWinner(playersEndWithEqualStonesBoard));
    }

    @Test
    public void isMoveValidReturnsTrueWhenMoveWouldLeaveAllStoneCountsGTEZeroTest() throws Exception {
        List<Integer> board = Lists.newArrayList(2,4,3,5);
        List<Integer> move = Lists.newArrayList(0,-2,-1,0);

        assertTrue("Expected move to be declared valid", GameSolver.INSTANCE.isValidMove(board, move));
    }

    @Test
    public void isMoveValidReturnsTrueWhenMoveWouldLeaveAllStoneCountsEqualToZeroTest() throws Exception {
        List<Integer> board = Lists.newArrayList(2,4,3,5);
        List<Integer> move = Lists.newArrayList(-2,-4,-3,-5);

        assertTrue("Expected move to be declared valid", GameSolver.INSTANCE.isValidMove(board, move));
    }

    @Test
    public void isMoveValidReturnsFalseWhenMoveWouldLeaveOneStoneCountLessThanZeroTest() throws Exception {
        List<Integer> board = Lists.newArrayList(2,4,3,5);
        List<Integer> move = Lists.newArrayList(0,-1,-4,-2);

        assertFalse("Expected move to be declared invalid", GameSolver.INSTANCE.isValidMove(board, move));
    }

    @Test
    public void isMoveValidReturnsFalseWhenMoveWouldLeaveMoreThanOneStoneCountLessThanZeroTest() throws Exception {
        List<Integer> board = Lists.newArrayList(2,4,3,5);
        List<Integer> move = Lists.newArrayList(-3,-1,-4,-2);

        assertFalse("Expected move to be declared invalid", GameSolver.INSTANCE.isValidMove(board, move));
    }

    @Test
    public void checkBoardAndMoveSizeInSyncThrowsIAEWhenSizesAreNotEqualTest() throws Exception {
        List<Integer> board = Lists.newArrayList(1,2,3);
        List<Integer> move = Lists.newArrayList(2,3);

        assertNotEquals(board.size(), move.size());

        boolean threwAsExpected = false;

        try {
            GameSolver.INSTANCE.checkBoardAndMoveSizeInSync(board, move);
        } catch (IllegalArgumentException e) {
            threwAsExpected = true;
        }

        assertTrue("Check did not throw expected exception due to unequal board and move lengths",threwAsExpected);
    }

    @Test
    public void checkBoardAndMoveSizeInSyncDoesNotThrowExceptionWhenSizesAreEqualTest() throws Exception {
        List<Integer> board = Lists.newArrayList(1,2,3);
        List<Integer> move = Lists.newArrayList(4,2,3);

        assertEquals(board.size(), move.size());

        boolean didNotThrow = true;

        try {
            GameSolver.INSTANCE.checkBoardAndMoveSizeInSync(board, move);
        } catch (Exception e) {
            didNotThrow = false;
        }

        assertTrue("Unexpected exception thrown with equal board and move lengths", didNotThrow);
    }

    @Test
    public void applyMoveReturnsExpectedNewBoardTest() throws Exception {
        List<Integer> board = Lists.newArrayList(6,4,2,5);
        List<Integer> move = Lists.newArrayList(-3,0,-1,-2);
        List<Integer> expectedNewBoard = Lists.newArrayList(3,4,1,3);

        List<Integer> modifiedBoard = GameSolver.INSTANCE.applyMove(board, move);

        assertEquals("Board with move applied did not result in expected board", expectedNewBoard, modifiedBoard);
        assertNotSame("Board after move applied expected to be different object than original board", board, modifiedBoard);
    }

    @Test
    public void getValidMovesReturnsAllMovesIfAllAreValidTest() throws Exception {
        List<Integer> board = Lists.newArrayList(6,4,2,5);
        List<Integer> validMove1 = Lists.newArrayList(-3,0,-1,-2);
        List<Integer> validMove2 = Lists.newArrayList(-2,0,0,-4);
        List<Integer> validMove3 = Lists.newArrayList(-1,-3,2,-1);

        GameSolver solver = GameSolver.INSTANCE;
        assertTrue(solver.isValidMove(board, validMove1));
        assertTrue(solver.isValidMove(board, validMove2));
        assertTrue(solver.isValidMove(board, validMove3));

        List<List<Integer>> allPossibleMoves = Lists.newArrayList(validMove1, validMove2, validMove3);
        assertThat(solver.getValidMoves(board, allPossibleMoves), hasItems(validMove1, validMove2, validMove3));
    }

    @Test
    public void getValidMovesReturnsOnlyValidMovesIfAllAreNotValidTest() throws Exception {
        List<Integer> board = Lists.newArrayList(6,4,2,5);
        List<Integer> validMove1 = Lists.newArrayList(-3,0,-1,-2);
        List<Integer> validMove2 = Lists.newArrayList(-1,-3,2,-1);
        List<Integer> invalidMove = Lists.newArrayList(-2,0,-3,-4); // not valid

        GameSolver solver = GameSolver.INSTANCE;
        assertTrue(solver.isValidMove(board, validMove1));
        assertTrue(solver.isValidMove(board, validMove2));
        assertFalse(solver.isValidMove(board, invalidMove));

        List<List<Integer>> allPossibleMoves = Lists.newArrayList(validMove1, validMove2, invalidMove);
        assertThat(solver.getValidMoves(board, allPossibleMoves), hasItems(validMove1, validMove2));
        assertThat(solver.getValidMoves(board, allPossibleMoves), not(hasItems(invalidMove)));
    }

    @Test
    public void getValidMovesReturnsEmptyListIfNoMovesAreValidTest() throws Exception {
        List<Integer> board = Lists.newArrayList(6,4,2,5);
        List<Integer> invalidMove1 = Lists.newArrayList(-3,0,-1,-6);
        List<Integer> invalidMove2 = Lists.newArrayList(-1,-5,2,-1);
        List<Integer> invalidMove3 = Lists.newArrayList(-2,0,-3,-4);

        GameSolver solver = GameSolver.INSTANCE;
        assertFalse(solver.isValidMove(board, invalidMove1));
        assertFalse(solver.isValidMove(board, invalidMove2));
        assertFalse(solver.isValidMove(board, invalidMove3));

        List<List<Integer>> allPossibleMoves = Lists.newArrayList(invalidMove1, invalidMove2, invalidMove3);
        assertTrue(solver.getValidMoves(board, allPossibleMoves).isEmpty());
    }

    @Test
    public void exampleCaseReturnsExpectedPlayerWinCountsTest() throws Exception {
/*
        Example: Let's say the game begins with a board of [6, 4, 2, 4]. These are the available moves provided:
        1. [-2, -2, 1, 0]
        2. [-4, -4, 0 ,0]
        3. [0, 0, -2, -2]
*/
        List<Integer> board = Lists.newArrayList(6,4,2,4);
        List<List<Integer>> allMoves = Lists.newArrayList(
                Lists.newArrayList(-2,-2,1,0),
                Lists.newArrayList(-4,-4,0,0),
                Lists.newArrayList(0,0,-2,-2)
        );

        GameSolver.Solution solution = GameSolver.INSTANCE.findPossibleWinningStrategies(board, allMoves);
        assertEquals(3, solution.getPlayer1WinTotal());
        assertEquals(2, solution.getPlayer2WinTotal());
    }

    @Test
    public void onlyOnePossiblePermutationPlayer1WinsTest() throws Exception {
        List<Integer> board = Lists.newArrayList(6, 4, 2, 4);
        List<Integer> validMove = Lists.newArrayList(0,0,-1,0);
        List<List<Integer>> moves = new ArrayList<>();
        moves.add(validMove);

        // Player 1 move - 6,4,1,4
        // Player 2 move - 6,4,0,4 (game over)
        // Player 1 wins

        GameSolver.Solution solution = GameSolver.INSTANCE.findPossibleWinningStrategies(board, moves);
        assertEquals(1, solution.getPlayer1WinTotal());
        assertEquals(0, solution.getPlayer2WinTotal());
    }

    @Test
    public void onlyOnePossiblePermutationPlayer2WinsTest() throws Exception {
        List<Integer> board = Lists.newArrayList(4, 4, 2, 6);
        List<Integer> validMove = Lists.newArrayList(0,0,-1,0);
        List<List<Integer>> moves = new ArrayList<>();
        moves.add(validMove);

        // Player 1 move - 4,4,1,6
        // Player 2 move - 4,4,0,6 (game over)
        // Player 2 wins

        GameSolver.Solution solution = GameSolver.INSTANCE.findPossibleWinningStrategies(board, moves);
        assertEquals(0, solution.getPlayer1WinTotal());
        assertEquals(1, solution.getPlayer2WinTotal());
    }
}
