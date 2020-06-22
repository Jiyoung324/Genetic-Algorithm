import java.util.Arrays;
import java.util.Random;

public class main {

    public static int[] init() { // -15~15 사이의 임의의 기울기a 8개를 뽑음
               Random r = new Random();
               int[] a = new int[8];

               for(int i=0; i<8; i++) {
                        a[i] = r.nextInt(30)-15;
                        for(int j=0; j<i ; j++){
                            if(a[i]==a[j])
                            {i--;
                            break;}
                        }
                     }
               return a;
            }

    public static double[] fitness(int[] a , double[] xd, double[] yd){ //MSE 사용한 적합도 계산 , 적합도 계산할 a집합, x데이터, y데이터를 인자로 받음
        double[] ft = new double[a.length]; // 기울기a 집합에 대한 적합도
        double[] temp_y= new double[yd.length]; // a일때, xd에 대한 y'값
        double mse=0,temp=0;

        for(int i =0; i<a.length; i++) {
            for (int j = 0; j < xd.length; j++) {
                temp = a[i] * xd[j] +17 ; // y = a * x + 17
                temp_y[j] = temp;
                mse = (yd[j] - temp_y[j]) * (yd[j] - temp_y[j]);// MSE적합도 = (y-y')^2

                ft[i] += mse; // 각 x에대한 mse를 모두 더한 값이 MSE
            }mse=0;
        }
            return ft;
        }


    public static int[] selection(int[] a , double[] ft) { // 적합도 판별 후 룰렛휠 방법을 사용한 선택연산
        double sum = 0;
        for(int i =0; i< ft. length; i++){
            sum += ft[i];
        }
        double[] mseft = new double[ft.length]; // MSE 기준 적합도 ( MSE적합도가 작을수록 오류가 더 작기 때문에 룰렛의 더 큰 비중을 가져야함, 따라서 선택연산을 위한 적합도로 다시 나타냄)
        double msesum = 0;

        for(int j =0; j<ft.length;j++) {
            mseft[j] = sum - ft[j]; // MSE 기준 적합도로 나타내기 위하여 전체 적합도(sum)에서 빼줌
            msesum += mseft[j]; // MSE 기준 적합도 sum
        }

        double[] ratio = new double[ft.length]; // 룰렛 비율

        for(int i=0; i<ft.length; i++) {
                if(i==0) ratio[i] = (double)mseft[i] / (double)msesum;
                else ratio[i] = ratio[i-1] + (double)mseft[i] / (double)msesum; // 앞의 비율에 더하여 영역을 나타냄
                     }

            int[] sx = new int[ft.length];
            Random r = new Random();
                for(int i=0; i<ft.length; i++) {
                        double pick = r.nextDouble(); // 랜덤으로 돌려 해당하는 영역의 a를 선택함

                         if(pick < ratio[0]) sx[i] = a[0];
                         else if(pick < ratio[1]) sx[i] = a[1];
                         else if(pick < ratio[2]) sx[i] = a[2];
                         else if(pick < ratio[3]) sx[i] = a[3];
                         else if(pick < ratio[4]) sx[i] = a[4];
                         else if(pick < ratio[5]) sx[i] = a[5];
                         else if(pick < ratio[6]) sx[i] = a[6];
                         else sx[i] = a[7];
                     }

                return sx;
         }


    public static int[] crossOver(int[] x) { // 선택된 a 의 교차연산

        int[] arr = new int[x.length];
            for(int i=0; i<x.length; i+=2) { // 앞 뒤로 더하고 빼서 평균으로 나누어 교차
                arr[i] = (x[i] + x[i+1])/2;
                arr[i+1] =(x[i] - x[i+1])/2;
                   }
               return arr;
             }


    public static int[] mutation(int[] x) { // 변이 연산
        Random r = new Random();
        int[] arr = new int[x.length];
        double mupick = r.nextDouble();

        for(int i=0; i<x.length; i++) {
            if(mupick< 0.125) { // 돌연변이율 8개중에 한개 , 즉 1/8 = 0.125
                arr[i] = x[i] *(-1); // 돌연변이율보다 작을 경우 돌연변이 발생, 부호 변경
                }
            else
                arr[i] = x[i];
              }
               return arr ;
            }

    public static int evaluaion(double[] a, double[] x) { // 적합도 평가 연산

        double ftmin = a[0];
        double fftmin = x[0];

        for(int i=0; i<a.length;i++){
            if(ftmin>a[i])
                ftmin = a[i];
            if(fftmin>x[i])
                fftmin = x[i];
        }

        int ftex=0;
        if(ftmin <= fftmin) // 이전 a 집합의 적합도의 최저값 과 유전(선택,교차,변이) 이후 a 집합의 적합도의 최저값 비교 , 유전한 적합도의 최저값이 이전과 작거나 같을 경우 유전 종료
            ftex = 1;
        else
            ftex = 2;

        return ftex;
    }



     public static void main(String[] args) {

         double[] xd = {1 ,1.1 ,1.3 ,1.5 ,1.7 ,2 ,2.2 ,2.4 ,2.6 ,2.8 ,3 ,3.1 ,3.3 ,3.5,3.7,3.9, 4 ,4.3 ,4.5, 4.6 ,4.9 ,5 ,5.1 ,5.3, 5.4 ,5.7, 6 ,6.3 ,6.5 ,6.9 ,7 ,7.3 ,7.4 ,7.5 ,7.8 ,8 ,8.1 ,8.4, 8.7, 9 ,9.1 ,9.7 ,9.9};
         double[] yd = {16 ,15.9 ,16.3 ,16.5 ,16.8 ,15 ,15.8,15.6 ,15.3 ,16 ,15 ,15.2 ,15.8,15.6,16.2,15.5, 14 ,14.7 ,13.8, 14.2 ,14.5 ,10 ,10.5 ,8.7, 11 ,9.2, 8 ,9 ,7.8 ,8.3 ,4 ,4.5 ,3.5 ,3.8 ,3.2 ,5 ,6 ,5.2,4.2, 7 ,6.3 ,7.2 ,7};

        int[] a = init();
        double[] ft = fitness(a,xd,yd);

        for(int i=0; i<1000; i++) {

            int[] sx = selection(a, ft);
            int[] cx = crossOver(sx);
            int[] mx = mutation(cx);


            double[] fft = fitness(mx, xd, yd);
            int ex = evaluaion(ft, fft);


            if (ex == 1) { // 적합도 개선 되지 않았을 경우 반복 종료
                for (int j = 0; j < mx.length; j++) {
                    System.out.println("a = " + a[j] + " 일 때, 적합도 :" + Math.round(ft[j]));
                    System.out.println("a = " + mx[j] + "일 때, 적합도 :" + Math.round(fft[j]));
                }
                break;
            }
            else // 적합도 개선되면 유전 반복
                for (int j = 0; j < mx.length; j++) {
                    System.out.println("a = " + mx[j] + "일 때, 적합도 :" + Math.round(fft[j]));
                    System.out.println();
                }
                System.out.println("유전 계속");
                    a = mx;
                    ft = fft;
        }
    }

}
