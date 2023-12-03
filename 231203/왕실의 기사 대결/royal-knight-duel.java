import java.util.*;
import java.io.*;

public class Main {
    static int L, N, Q;
	static int[][] board, knightBoard;
	static int[] dx = {-1, 0, 1, 0};
	static int[] dy = {0, 1, 0, -1};
//	static List<Knight>[][] knightBoard;
	static List<Knight> knights;
	static List<Integer> who;
	static class Knight {
		int idx, x, y, height, width, hp, damage;
		
		public Knight(int idx, int x, int y, int height, int width, int hp, int damage) {
			this.idx = idx;
			this.x = x;
			this.y = y;
			this.height = height;
			this.width = width;
			this.hp = hp;
			this.damage = damage;
		}
	}
	static boolean canWeGo;
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		// L x L 크기의 체스판
		L = Integer.parseInt(st.nextToken());
		board = new int[L + 1][L + 1];
		knightBoard = new int[L + 1][L + 1];
		// 기사의 수
		N = Integer.parseInt(st.nextToken());
		// 왕의 명령의 수
		Q = Integer.parseInt(st.nextToken());
		
		// 체스판의 상태
		for (int i = 1; i <= L; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j = 1; j <= L; j++) {
				// 0 이라면 빈 칸, 1 이라면 함정, 2 라면 벽을 의미 
				board[i][j] = Integer.parseInt(st.nextToken());
			}
		}
//		knightBoard = new ArrayList[L + 1][L + 1];
		knights = new ArrayList<>();
//		for (int i = 1; i <= L; i++) {
//			st = new StringTokenizer(br.readLine());
//			for (int j = 1; j <= L; j++) {
//				knightBoard[i][j] = new ArrayList<>();
//			}
//		}
		
		// 기사의 정보
		for (int i = 1; i <= N; i++) {
			st = new StringTokenizer(br.readLine());
			int r = Integer.parseInt(st.nextToken());
			int c = Integer.parseInt(st.nextToken());
			int h = Integer.parseInt(st.nextToken());
			int w = Integer.parseInt(st.nextToken());
			int k = Integer.parseInt(st.nextToken());
//			knightBoard[r][c].add(new Knight(i, r, c, h, w, k));
			fillKnight(i, r, c, h, w);
			knights.add(new Knight(i, r, c, h, w, k, 0));
		}
		
//		for (int i = 1; i <= L; i++) System.out.println(Arrays.toString(knightBoard[i]));
		
		// 왕의 명령의 정보
		for (int i = 0; i < Q; i++) {
			st = new StringTokenizer(br.readLine());
			int idx = Integer.parseInt(st.nextToken());
			int d = Integer.parseInt(st.nextToken());
			who = new ArrayList<>();
			// 1. 밀려날 위치에 누가 있는지 파악
			canWeGo = true;
			whoAreYou(idx, d);
			
			// 2. 벽이 있는지 파악 => true, false 반환해서 true면 continue, false면 그대로 밀기
			
			
			// 3. 밀림 처리 하고, 체력 깎기.
			if (canWeGo) {
				for (int j = who.size() - 1; j >= 0; j--) {
//					Knight knight = knights.get(who.get(j)-1);
					moveAndDamage(who.get(j), d);
				}
				justMove(idx, d);
			}
		}
		int res = 0;
		for (Knight k : knights) {
//			System.out.println("idx : " + k.idx + " " + "hp : " + k.hp + " " + "damage : " + k.damage);
			if (k.hp != 0) res += k.damage;
		}
		System.out.println(res);
	}
	public static void justMove(int idx, int dir) {
		Knight knight = knights.get(idx-1);
		// 위
		if (dir == 0) {
			for (int j = knight.y; j < knight.y + knight.width; j++) {
				knightBoard[knight.x+knight.height-1][j] = 0;
				knightBoard[knight.x-1][j] = idx;
			}
			knights.set(idx-1, new Knight(idx, knight.x - 1, knight.y, knight.height, knight.width, knight.hp, knight.damage));
		// 오른쪽
		} else if (dir == 1) {
			for (int i = knight.x; i < knight.x + knight.height; i++) {
				knightBoard[i][knight.y] = 0;
				knightBoard[i][knight.y+knight.width] = idx;
			}
			knights.set(idx-1, new Knight(idx, knight.x, knight.y + 1, knight.height, knight.width, knight.hp, knight.damage));
		// 아래
		} else if (dir == 2) {
			for (int j = knight.y; j < knight.y + knight.width; j++) {
				knightBoard[knight.x][j] = 0;
				knightBoard[knight.x+knight.height][j] = idx;
			}
			knights.set(idx-1, new Knight(idx, knight.x + 1, knight.y, knight.height, knight.width, knight.hp, knight.damage));
		// 왼쪽
		} else {
			for (int i = knight.x; i < knight.x + knight.height; i++) {
				knightBoard[i][knight.y+knight.width-1] = 0;
				knightBoard[i][knight.y-1] = idx;
			}
			knights.set(idx-1, new Knight(idx, knight.x, knight.y - 1, knight.height, knight.width, knight.hp, knight.damage));
		}
	}
	
	public static void moveAndDamage(int idx, int dir) {
		Knight knight = knights.get(idx-1);
		int hp = knight.hp;
		// 위
		if (dir == 0) {
			for (int j = knight.y; j < knight.y + knight.width; j++) {
				knightBoard[knight.x+knight.height-1][j] = 0;
				knightBoard[knight.x-1][j] = idx;
			}
			boolean flag = false;
			loop: for (int i = knight.x - 1; i <= knight.x + knight.height - 1; i++) {
				for (int j = knight.y; j < knight.y + knight.width; j++) {
					if (board[i][j] == 1) hp--;
					if (hp <= 0) {
						flag = true;
						
						break loop;
					}
				}
			}
			if (flag) {
				for (int i = knight.x - 1; i <= knight.x + knight.height - 1; i++) {
					for (int j = knight.y; j < knight.y + knight.width; j++) {
						knightBoard[i][j] = 0;
					}
				}
				knights.set(idx-1, new Knight(idx, 0, 0, 0, 0, 0, 0));
			} else knights.set(idx-1, new Knight(idx, knight.x - 1, knight.y, knight.height, knight.width, hp, knight.damage + knight.hp - hp));
		// 오른쪽
		} else if (dir == 1) {
			for (int i = knight.x; i < knight.x + knight.height; i++) {
				knightBoard[i][knight.y] = 0;
				knightBoard[i][knight.y+knight.width] = idx;
			}
			boolean flag = false;
			loop: for (int i = knight.x; i < knight.x + knight.height; i++) {
				for (int j = knight.y + 1; j <= knight.y + knight.width; j++) {
					if (board[i][j] == 1) hp--;
					if (hp <= 0) {
						flag = true;
						
						break loop;
					}
				}
			}
			if (flag) {
				for (int i = knight.x; i < knight.x + knight.height; i++) {
					for (int j = knight.y + 1; j <= knight.y + knight.width; j++) {
						knightBoard[i][j] = 0;
					}
				}
				knights.set(idx-1, new Knight(idx, 0, 0, 0, 0, 0, 0));
			} else knights.set(idx-1, new Knight(idx, knight.x, knight.y + 1, knight.height, knight.width, hp, knight.damage + knight.hp - hp));
		// 아래
		} else if (dir == 2) {
			for (int j = knight.y; j < knight.y + knight.width; j++) {
				knightBoard[knight.x][j] = 0;
				knightBoard[knight.x+knight.height][j] = idx;
			}
			boolean flag = false;
			loop: for (int i = knight.x + 1; i <= knight.x + knight.height; i++) {
				for (int j = knight.y; j < knight.y + knight.width; j++) {
					if (board[i][j] == 1) hp--;
					if (hp <= 0) {
						flag = true;
						
						break loop;
					}
				}
			}
			if (flag) {
				for (int i = knight.x + 1; i <= knight.x + knight.height; i++) {
					for (int j = knight.y; j < knight.y + knight.width; j++) {
						knightBoard[i][j] = 0;
					}
				}
				knights.set(idx-1, new Knight(idx, 0, 0, 0, 0, 0, 0));
			} else knights.set(idx-1, new Knight(idx, knight.x + 1, knight.y, knight.height, knight.width, hp, knight.damage + knight.hp - hp));
		// 왼쪽
		} else {
			for (int i = knight.x; i < knight.x + knight.height; i++) {
				knightBoard[i][knight.y+knight.width-1] = 0;
				knightBoard[i][knight.y-1] = idx;
			}
			boolean flag = false;
			loop: for (int i = knight.x; i < knight.x + knight.height; i++) {
				for (int j = knight.y - 1; j <= knight.y + knight.width - 1; j++) {
					if (board[i][j] == 1) hp--;
					if (hp <= 0) {
						flag = true;
						
						break loop;
					}
				}
			}
			if (flag) {
				for (int i = knight.x; i < knight.x + knight.height; i++) {
					for (int j = knight.y - 1; j <= knight.y + knight.width - 1; j++) {
						knightBoard[i][j] = 0;
					}
				}
				knights.set(idx-1, new Knight(idx, 0, 0, 0, 0, 0, 0));
			} else knights.set(idx-1, new Knight(idx, knight.x, knight.y - 1, knight.height, knight.width, hp, knight.damage + knight.hp - hp));
		}
	}
	
	public static void whoAreYou(int index, int dir) {
		Knight knight = knights.get(index-1);
		// 위
		if (dir == 0) {
			if (knight.x - 1 < 1) {
				canWeGo = false;
				return;
			}
			int next = 0;
			for (int j = knight.y; j < knight.y + knight.width; j++) {
				if (board[knight.x - 1][j] == 2) {
					canWeGo = false;
					return;
				}
				if (knightBoard[knight.x - 1][j] != 0 && knightBoard[knight.x - 1][j] != next) {
					next = knightBoard[knight.x - 1][j];
					who.add(knightBoard[knight.x - 1][j]);
					whoAreYou(knightBoard[knight.x - 1][j], dir);
				}
			}
		// 오른쪽
		} else if (dir == 1) {
			if (knight.y + knight.width > L) {
				canWeGo = false;
				return;
			}
			int next = 0;
			for (int i = knight.x; i < knight.x + knight.height; i++) {
				if (board[i][knight.y + knight.width] == 2) {
					canWeGo = false;
					return;
				}
				if (knightBoard[i][knight.y + knight.width] != 0 && knightBoard[i][knight.y + knight.width] != next) {
					next = knightBoard[i][knight.y + knight.width];
					who.add(knightBoard[i][knight.y + knight.width]);
					whoAreYou(knightBoard[i][knight.y + knight.width], dir);
					
				}
			}
		// 아래
		} else if (dir == 2) {
			if (knight.x + knight.height > L) {
				canWeGo = false;
				return;
			}
			int next = 0;
			for (int j = knight.y; j < knight.y + knight.width; j++) {
				if (board[knight.x + knight.height][j] == 2) {
					canWeGo = false;
					return;
				}
				if (knightBoard[knight.x + knight.height][j] != 0 && knightBoard[knight.x + knight.height][j] != next) {
					next = knightBoard[knight.x + knight.height][j];
					who.add(knightBoard[knight.x + knight.height][j]);
					whoAreYou(knightBoard[knight.x + knight.height][j], dir);
					
				}
			}
		// 왼쪽
		} else {
			if (knight.y - 1 < 1) {
				canWeGo = false;
				return;
			}
			int next = 0;
			for (int i = knight.x; i < knight.x + knight.height; i++) {
				if (board[i][knight.y - 1] == 2) {
					canWeGo = false;
					return;
				}
				if (knightBoard[i][knight.y - 1] != 0 && knightBoard[i][knight.y - 1] != next) {
					next = knightBoard[i][knight.y - 1];
					who.add(knightBoard[i][knight.y - 1]);
					whoAreYou(knightBoard[i][knight.y - 1], dir);
					
				}
			}
		}
	}
	
	public static void fillKnight(int i, int r, int c, int h, int w) {
		for (int p = r; p < r + h; p++) {
			for (int q = c; q < c + w; q++) {
				knightBoard[p][q] = i;
			}
		}
	}
}