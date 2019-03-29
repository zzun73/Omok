package omok;
	
public class Omok {
	public static void main(String[] args) {
		Board board = new Board();
		System.out.println(board.toString());
		Move move = new Move('x',"j10");
		board.update(move);
		System.out.println(move.getX());
		System.out.println(move.getY());
		System.out.println(board.toString());
	}
}