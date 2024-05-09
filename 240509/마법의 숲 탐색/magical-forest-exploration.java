import java.io.*;
import java.util.*;

public class Main {
    static class Golem{
        int midX, midY, exit;
        public Golem(int midX, int midY, int exit){
            this.midX = midX;
            this.midY = midY;
            this.exit = exit;
        }
        public static Golem down(Golem g){
            return new Golem(g.midX+1, g.midY, g.exit);
        }
        public static Golem left(Golem g){
            return new Golem(g.midX+1, g.midY-1, (g.exit+3)%4);
        }
        public static Golem right(Golem g){
            return new Golem(g.midX+1, g.midY+1, (g.exit+1)%4);
        }
    }

    static int[][] forest; // 숲의 이동가능한 위치 & 어떤 골렘이 위치해있는지 저장.
    static boolean[][] isExit;
    static int R,C;
    static int[] maxSouth;
    public static void main(String[] args) throws Exception{
        // 여기에 코드를 작성해주세요.

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String[] s = br.readLine().split(" ");

        R = Integer.parseInt(s[0]); // 가로 몇 행
        C = Integer.parseInt(s[1]); // 세로 몇 열
        int K = Integer.parseInt(s[2]); // 정령의 수

        int sum = 0;
        forest = new int[R][C];
        isExit = new boolean[R][C];
        for(int i=0;i<R;i++){
            Arrays.fill(forest[i],-1);
        }

        maxSouth = new int[K];
        for(int i=0;i<K;i++){
            s = br.readLine().split(" ");

            int c = Integer.parseInt(s[0]); // 몇 열로 내려오는지
            int d = Integer.parseInt(s[1]); // 출구 방향
            // (0,1,2,3) -> (북,동,남,서)
            
            // 주어진 골렘 이동
            Golem g = moveGolem(i,c,d);
            if(g==null){
                Arrays.fill(maxSouth,0); // 기존의 골렘들 다 치우기.
                for(int r=0;r<R;r++){
                    Arrays.fill(forest[r],-1);
                }
                for(int r=0;r<R;r++){
                    Arrays.fill(isExit[r],false);
                }
                continue;
            }
            // System.out.println(g.midX+" "+g.midY+" "+g.exit);

            // 현재 골렘의 출구가 다른 골렘과 맞다아 있는지 확인.
            // 1. 있으면 -> 그 번호의 골렘이 이동한 남쪽 위치 받음.
            // 2. 현재 골렘에서 이동할 수 있는 가장 남쪽 행.        => 2개의 최대값
            // checkMaxSouth(g,i);
            bfs(g.midX, g.midY, i);
            sum += maxSouth[i]+1; // 0~R-1 로 되어있어서 +1 해줌.

            // System.out.println(maxSouth[i]+1);

            // for(int x=0;x<R;x++){
            //     System.out.println(Arrays.toString(forest[x]));
            // }
        }

        // 모든 정령이 이동한 후의 sum값 출력.
        System.out.println(sum);
    }
    static Golem moveGolem(int idx, int col, int dir){
        
        Golem g = new Golem(-2, col-1, dir);
        // System.out.println(g.midX+" "+g.midY+" "+g.exit);

        Golem ng;
        while(true){
            // 아직 골렘이 전부 다 들어오기 전일 때
            if(g.midX==-2){ // 완전 처음.
                if(forest[0][g.midY]==-1){
                    g = Golem.down(g); continue;
                } else if(g.midY-2>=0 && forest[0][g.midY-1]==-1){
                    g = Golem.left(g); continue;
                } else if(g.midY+2<C && forest[0][g.midY+1]==-1){
                    g = Golem.right(g); continue;
                } else{
                    return null;
                }
            }
            if(g.midX==-1){
                if(g.midY-1>=0 && g.midY+1<C && 
                forest[1][g.midY]==-1 && forest[0][g.midY-1]==-1 && forest[0][g.midY+1]==-1){
                    g = Golem.down(g); continue;
                } else if(g.midY-2>=0 && forest[1][g.midY-1]==-1 && forest[0][g.midY-2]==-1){
                    g = Golem.left(g); continue;
                } else if(g.midY+2<C &&forest[1][g.midY+1]==-1 && forest[0][g.midY+2]==-1){
                    g = Golem.right(g); continue;
                } else{
                    return null;
                }
            }
            if(g.midX==0){
                if(forest[2][g.midY]==-1 && forest[1][g.midY-1]==-1 && forest[1][g.midY+1]==-1){
                    g = Golem.down(g); continue;
                } else if(g.midY-2>=0 && forest[0][g.midY-2]==-1 && forest[2][g.midY-1]==-1 && forest[1][g.midY-2]==-1){
                    g = Golem.left(g); continue;
                } else if(g.midY+2<C && forest[0][g.midY+2]==-1 && forest[2][g.midY+1]==-1 && forest[1][g.midY+2]==-1){
                    g = Golem.right(g); continue;
                } else{
                    return null;
                }
            }

            // 1. 남쪽 이동
            ng = Golem.down(g);
            if(ng.midX+1<R && ng.midY-1>=0 && ng.midY+1<C &&
            forest[ng.midX+1][ng.midY]==-1 && forest[ng.midX-1][ng.midY]==-1 
            && forest[ng.midX][ng.midY+1]==-1 && forest[ng.midX][ng.midY-1]==-1){
                g = ng;
                continue;
            }

            // 2. 안되면 서쪽 이동
            ng = Golem.left(g);
            if(ng.midX+1<R && ng.midY-1>=0 && ng.midY+1<C &&
            forest[g.midX][g.midY-2]==-1 &&
            forest[ng.midX+1][ng.midY]==-1 && forest[ng.midX-1][ng.midY]==-1 
            && forest[ng.midX][ng.midY+1]==-1 && forest[ng.midX][ng.midY-1]==-1){
                g = ng;
                continue;
            }

            // 3. 안되면 동쪽 이동
            ng = Golem.right(g);
            if(ng.midX+1<R && ng.midY-1>=0 && ng.midY+1<C &&
            forest[g.midX][g.midY+2]==-1 &&
            forest[ng.midX+1][ng.midY]==-1 && forest[ng.midX-1][ng.midY]==-1 
            && forest[ng.midX][ng.midY+1]==-1 && forest[ng.midX][ng.midY-1]==-1){
                g = ng;
                continue;
            }

            // 4. 다 안되면 현재 골렘 위치 갱신하고 종료.
            forest[g.midX][g.midY]=idx;
            forest[g.midX+1][g.midY]=idx;
            forest[g.midX-1][g.midY]=idx;
            forest[g.midX][g.midY+1]=idx;
            forest[g.midX][g.midY-1]=idx;
            maxSouth[idx] = g.midX+1; // 현재 골렘의 가운데 +1만큼 내려옴.

            int exitX=g.midX, exitY=g.midY;
            switch(g.exit){
                case 0:{ exitX=exitX-1; break; }
                case 1:{ exitY=exitY+1; break; }
                case 2:{ exitX=exitX+1; break; }
                case 3:{ exitY=exitY-1; break; }
            }
            isExit[exitX][exitY] = true;

            break;
        }

        return g;
    }
    static int bfs(int x, int y, int idx){
        int[] dx = {1,-1,0,0};
        int[] dy = {0,0,1,-1};

        int result = x;

        Queue<int[]> q = new LinkedList<>();
        boolean[][] visited = new boolean[R][C];
        q.add(new int[]{x,y});
        visited[x][y]=true;

        while(!q.isEmpty()){
            int[] now = q.poll();

            for(int i=0;i<4;i++){
                int nx = now[0]+dx[i];
                int ny = now[1]+dy[i];

                if(nx<0 || nx>=R || ny<0 || ny>=C || visited[nx][ny]) continue;
                // 같은 골렘 안에 있거나, 출구쪽이면 이동 가능.
                if(forest[nx][ny]==forest[now[0]][now[1]] || (forest[nx][ny]!=-1 && isExit[now[0]][now[1]])){
                    q.add(new int[]{nx,ny});
                    visited[nx][ny]=true;
                    result = Math.max(result, nx);
                }
            }
        }

        maxSouth[idx] = result;
        return result;
    }
    // static void checkMaxSouth(Golem g, int idx){
    //     int x = g.midX;
    //     int y = g.midY;

    //     switch (g.exit) { // 출구 방향으로 이동.
    //         case 0:{ x--; break; }
    //         case 1:{ y++; break; }
    //         case 2:{ x++; break; }
    //         case 3:{ y--; break; }
    //     }

    //     isExit[x][y] = true;

    //     int[] dx = {1,-1,0,0};
    //     int[] dy = {0,0,1,-1};

    //     for(int i=0;i<4;i++){ // 상하좌우를 보면서 기존의 골렘이랑 연결되어있고, 그 골렘을 통해 남쪽으로 내려간 위치가 더 크면 갱신.
    //         int nx = x+dx[i];
    //         int ny = y+dy[i];

    //         if(nx>=0 && nx<R && ny>=0 && ny<C && forest[nx][ny]>=0){
    //             maxSouth[idx] = Math.max(maxSouth[idx], maxSouth[forest[nx][ny]]);
    //         }
    //     }
    // }
}