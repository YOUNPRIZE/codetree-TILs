import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Main {
    static int N, M, P, C, D;
    static int[][] map;
    // 상우하좌 순서
    static int[] dx = {-1, 0, 1, 0, -1, 1, 1, -1};
    static int[] dy = {0, 1, 0, -1, 1, 1, -1, -1};
    static int rx, ry;
    static List<Santa> santas;
    static class Santa implements Comparable<Santa> {
        int idx;
        int x;
        int y;
        int grade;
        boolean isAlive;
        int coma;

        public Santa(int idx, int x, int y, int grade, boolean isAlive, int coma) {
            this.idx = idx;
            this.x = x;
            this.y = y;
            this.grade = grade;
            this.isAlive = isAlive;
            this.coma = coma;
        }

        // 오름차순 정렬
        @Override
        public int compareTo(Santa o) {
            return idx - o.idx;
        }
    }
    static boolean globalFlag = false;
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken()); // 게임판의 크기
        M = Integer.parseInt(st.nextToken()); // 게임 턴 수
        P = Integer.parseInt(st.nextToken()); // 산타의 수
        C = Integer.parseInt(st.nextToken()); // 루돌프의 힘
        D = Integer.parseInt(st.nextToken()); // 산타의 힘

        map = new int[N + 1][N + 1];
        santas = new ArrayList<>();

        st = new StringTokenizer(br.readLine());
        // 루돌프의 초기 위치
        rx = Integer.parseInt(st.nextToken());
        ry = Integer.parseInt(st.nextToken());
        map[rx][ry] = -1;

        santas.add(new Santa(0, 0, 0, 0, false, 0));
        // 산타의 번호와 초기 위치
        for (int i = 0; i < P; i++) {
            st = new StringTokenizer(br.readLine());
            int idx = Integer.parseInt(st.nextToken());
            int sx = Integer.parseInt(st.nextToken());
            int sy = Integer.parseInt(st.nextToken());
            santas.add(new Santa(idx, sx, sy, 0, true, 0));
            map[sx][sy] = idx;
        }
        Collections.sort(santas);

        // 게임 실행
        for (int m = 1; m <= M; m++) {
            // 1. 루돌프 이동
            rudolfMove();
            if (globalFlag) break;
            // 2. 산타 차례대로 이동
            santaMove();
//            for (int j = 1; j <= N; j++) System.out.println(Arrays.toString(map[j]));
//            System.out.println();
            if (globalFlag) break;
            // 3. 살아있는 산타는 점수 획득
            for (int p = 1; p <= P; p++) {
                Santa santa = santas.get(p);
                if (santa.isAlive) {
                    if (santa.coma > 0) santas.set(p, new Santa(p, santa.x, santa.y, santa.grade+1, santa.isAlive, --santa.coma));
                    else santas.set(p, new Santa(p, santa.x, santa.y, santa.grade+1, santa.isAlive, santa.coma));
                }
//                System.out.println(santa.idx + "번 산타 : " + santa.grade + ", 코마 : " + santa.coma);
            }
            if (globalFlag) break;
        }

//        int res = 0;

        for (int i = 1; i <= P; i++) {
            Santa santa = santas.get(i);
//            res += santa.grade;
            System.out.print(santa.grade + " ");
        }
//        System.out.println(res);
    }
    public static boolean canContinue() {
        boolean flag = false;
        for (int i = 1; i <= P; i++) {
            Santa santa = santas.get(i);
            if (santa.isAlive) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    public static void santaMove() {
        // 1번부터 P번까지 차례대로 움직임. (O)
        // 산타는 루돌프에게 거리가 가장 가까워지는 방향으로 1칸 이동합니다. (O)
        // 산타는 다른 산타가 있는 칸이나 게임판 밖으로는 움직일 수 없습니다. (O)
        // 움직일 수 있는 칸이 없다면 산타는 움직이지 않습니다. (O)
        // 움직일 수 있는 칸이 있더라도 만약 루돌프로부터 가까워질 수 있는 방법이 없다면 산타는 움직이지 않습니다.
        // 산타는 상하좌우로 인접한 4방향 중 한 곳으로 움직일 수 있습니다. 이때 가장 가까워질 수 있는 방향이 여러 개라면, 상우하좌 우선순위에 맞춰 움직입니다.
        for (int p = 1; p <= P; p++) {
            Santa santa = santas.get(p);

            // 죽은 애는 skip
            if (!santa.isAlive || santa.coma > 0) continue;
            double curDir = Math.pow((santa.x - rx), 2) + Math.pow((santa.y - ry), 2);
            int idx = -1;
            for (int i = 0; i < 4; i++) {
                int nx = santa.x + dx[i];
                int ny = santa.y + dy[i];
                if (nx < 1 || ny < 1 || nx > N || ny > N) continue;
                if (map[nx][ny] > 0) continue;
                double moveDir = Math.pow(nx - rx, 2) + Math.pow(ny - ry, 2);
                if (moveDir < curDir) {
                    curDir = moveDir;
                    idx = i;
                }
            }
            if (idx != -1) {
                map[santa.x][santa.y] = 0;
                int nx = santa.x + dx[idx];
                int ny = santa.y + dy[idx];
                // 여기서 산타가 루돌프 박았을 때 경우 추가해줘야 함.
                if (map[nx][ny] == -1) {
                    int newIdx = (idx + 2) % 4;
                    int newX = nx + D * dx[newIdx];
                    int newY = ny + D * dy[newIdx];
                    int newGrade = santa.grade + D;
                    if (newX < 1 || newY < 1 || newX > N || newY > N) {
                        santas.set(p, new Santa(santa.idx, newX, newY, newGrade, false, 0));
                        if (!canContinue()) {
                            globalFlag = true;
                            return;
                        }
                    } else {
                        // 상호작용 추가해줘야함.
                        if (map[newX][newY] > 0) {
                            interaction(map[newX][newY], newX, newY, newIdx);
                            map[newX][newY] = santa.idx;
                            santas.set(p, new Santa(santa.idx, newX, newY, newGrade, true, 2));
                        } else {
                            map[newX][newY] = santa.idx;
                            santas.set(p, new Santa(santa.idx, newX, newY, newGrade, true, 2));
                        }
                    }
                } else {
                    if (santa.coma > 0) {
                        santas.set(p, new Santa(santa.idx, santa.x, santa.y, santa.grade, true, --santa.coma));
                    } else {
                        map[nx][ny] = santa.idx;
                        santas.set(p, new Santa(santa.idx, nx, ny, santa.grade, true, 0));
                    }
                }
            }
        }
    }
    public static void interaction(int idx, int x, int y, int d) {
        Santa santa = santas.get(idx);
        int nx = santa.x + dx[d];
        int ny = santa.y + dy[d];
        if (nx < 1 || ny < 1 || nx > N || ny > N) {
            santas.set(idx, new Santa(idx, nx, ny, santa.grade, false, 0));
            if (!canContinue()) {
                globalFlag = true;
//                return;
            }
            return;
        }
        if (map[nx][ny] > 0) {
            interaction(map[nx][ny], nx, ny, d);
        }
        map[nx][ny] = idx;
        santas.set(idx, new Santa(idx, nx, ny, santa.grade, true, santa.coma));
    }

    public static void rudolfMove() {
        int who = 0;
        int distance = Integer.MAX_VALUE;
        int r = Integer.MIN_VALUE;
        int c = Integer.MIN_VALUE;
        for (int i = 1; i <= P; i++) {
            Santa santa = santas.get(i);
            if (santa.isAlive) {
                double dis = Math.pow((rx - santa.x), 2) + Math.pow((ry - santa.y), 2);
                if (dis > distance) continue;
                else {
                    if (dis == distance) {
                        if (santa.x > r) {
                            who = i;
                            r = santa.x;
                            c = santa.y;
                        } else {
                            if (santa.x == r) {
                                if (santa.y > c) {
                                    who = i;
                                    c = santa.y;
                                }
                            }
                        }
                    } else {
                        who = i;
                        distance = (int) dis;
                        r = santa.x;
                        c = santa.y;
                    }
                }
            } else continue;
        }

        if (who != 0) {
            Santa santa = santas.get(who);
            int x = santa.x;
            int y = santa.y;
            int idx = -1;
            double curDir = Math.pow((rx - x), 2) + Math.pow((ry - y), 2);
            for (int i = 0; i < 8; i++) {
                int nx = rx + dx[i];
                int ny = ry + dy[i];
                double moveDir = Math.pow((nx - x), 2) + Math.pow((ny - y), 2);
                if (moveDir < curDir) {
                    curDir = moveDir;
                    idx = i;
                }
            }
            map[rx][ry] = 0;
            rx = rx + dx[idx];
            ry = ry + dy[idx];
            // 산타가 있는 경우
            if (map[rx][ry] > 0) {
//                int newIdx = (idx + 2) % 4;
                int newIdx = idx;
                int newX = santa.x + C * dx[newIdx];
                int newY = santa.y + C * dy[newIdx];
                int newGrade = santa.grade + C;
                //
                if (newX < 1 || newY < 1 || newX > N || newY > N) {
                    santas.set(santa.idx, new Santa(santa.idx, newX, newY, newGrade, false, 0));
                    if (!canContinue()) {
                        globalFlag = true;
                        return;
                    }
                } else {
                    // 상호작용 추가해줘야함.

                    if (map[newX][newY] > 0) {
                        // 루돌프와의 충돌 후 산타는 포물선의 궤적으로 이동하여 착지하게 되는 칸에서만 상호작용이 발생할 수 있습니다.
                        // 산타는 충돌 후 착지하게 되는 칸에 다른 산타가 있다면 그 산타는 1칸 해당 방향으로 밀려나게 됩니다.
                        // 그 옆에 산타가 있다면 연쇄적으로 1칸씩 밀려나는 것을 반복하게 됩니다.
                        // 게임판 밖으로 밀려나오게 된 산타의 경우 게임에서 탈락됩니다.
                        interaction(map[newX][newY], newX, newY, newIdx);
                        map[newX][newY] = santa.idx;
                        santas.set(santa.idx, new Santa(santa.idx, newX, newY, newGrade, true, 2));
                    } else {
                        map[newX][newY] = santa.idx;
                        santas.set(santa.idx, new Santa(santa.idx, newX, newY, newGrade, true, 2));
                    }
                }
                map[rx][ry] = -1;
            } else map[rx][ry] = -1;
        }
    }
}