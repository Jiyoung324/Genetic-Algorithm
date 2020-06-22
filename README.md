# Genetic-Algorithm

### * 사용 데이터

**수면시간 / (우울)유병율** 의 관계 로 *수면시간이 늘어날수록 유병율은 감소하는 경향*을 보임 <br>
  (참고 : ["7시간 수면 지키세요"…4시간 이하면 우울증 위험 4배(종합)](https://news.naver.com/main/read.nhn?mode=LSD&mid=sec&sid1=103&oid=001&aid=0009052925))

| 시간   | 0    | 1    | 1.1  | 2    | 2.2  | ..   | 4.5  | 5    | 6    | ..   | 7    | 7.5  | 8    | 8.7  | 9    | 9.7  |
| ------ | ---- | ---- | ---- | ---- | ---- | ---- | ---- | ---- | ---- | ---- | ---- | ---- | ---- | ---- | ---- | ---- |
| 유병율 | 17   | 16   | 15.9 | 15   | 15.8 | ..   | 13.8 | 10   | 8    | ..   | 4    | 3.8  | 5    | 4.2  | 7    | 7.2  |

위 표의 데이터와 같이 시간이 증가할수록 유병율은 감소하나 특정 지점(시간 =7) 이 지나면 다시 증가하는 데이터 값을 갖도록 임의의 데이터 셋을 설정하였다.
(데이터의 수가 많아 모두 적지 않았으며 코드에는 모든 데이터 값을 포함함)<br>

데이터의 산점도는 다음 그림과 같다.<br>
![GA 분산도](https://user-images.githubusercontent.com/63060298/85289216-0815ce00-b4d2-11ea-9005-b3d8c639dfb7.png)


### * 코드 설명


<pre><code>
        int[] a = init();
        double[] ft = fitness(a,xd,yd);
        
        for(int i=0; i<1000; i++)
            int[] sx = selection(a, ft);
            int[] cx = crossOver(sx);
            int[] mx = mutation(cx);

            double[] fft = fitness(mx, xd, yd);
            int ex = evaluaion(ft, fft);

            if (ex == 1) // 적합도 개선 되지 않았을 경우 반복종료
                break;
            else // 적합도 개선되면 유전반복
                 a = mx;
                 ft = fft;
                 
</code></pre>
 
 위의 코드는 메인 코드의 일부로 전체 코드의 흐름을 보여준다.
 
1. **init** 함수를 통해 임의의 a를 -15~15 범위에서 8개의 후보를 선택한다.
 
2. **fitness**  함수를 통해 선출된 a 집단의 적합도를 판별한다.
 <pre><code>
               for (int j = 0; j < xd.length; j++) {
                temp = a[i] * xd[j] +17 ; // y = a * x + 17
                temp_y[j] = temp;
                mse = (yd[j] - temp_y[j]) * (yd[j] - temp_y[j]);// MSE적합도 = (y-y')^2

                ft[i] += mse; // 각 x에대한 mse를 모두 더한 값이 MSE
            }mse=0;
 </code></pre>
 
3. **select** 함수를 통해 더 적합한 후보해의 비율이 큰 룰렛을 만든다.<br>
     즉, MSE에서는 적합도가 작을수록 더 적합함으로 적합도의 합계에서 해당 적합도를 빼준값을 선택연산에서 비율을 나타내기 위해 사용한다.
     따라서 MSE의 적합도가 작을수록 MSE적합도 mseft의 값은 더 크게 나타나므로 룰렛에서는 더 큰 비율을 차지한다.
     
<pre><code>
     for(int j = 0; j < ft.length;j++) {
            mseft[j] = sum - ft[j]; // MSE 기준 적합도로 나타내기 위하여 전체 적합도(sum)에서 빼줌
            msesum += mseft[j]; // MSE 기준 적합도 sum
        }

        double[] ratio = new double[ft.length]; // 룰렛 비율

        for(int i = 0; i < ft.length; i++) {
                if(i==0) ratio[i] = (double)mseft[i] / (double)msesum;
                else ratio[i] = ratio[i-1] + (double)mseft[i] / (double)msesum; // 앞의 비율에 더하여 영역을 나타냄
                     } </code></pre>

4. **crossOver** 함수를 통해 해집합 개체를 교차한다. 8개의 개체를 2개씩 더하고 뺀 뒤 평균으로 나누어 서로 교차한다.
<pre></code>
               for(int i = 0; i < x.length; i+=2) { // 앞 뒤로 더하고 빼서 평균으로 나누어 교차
                arr[i] = (x[i] + x[i+1])/2;
                arr[i+1] =(x[i] - x[i+1])/2;
                   } </code></pre>
 
5. **mutation** 함수를 통해 랜덤으로 선택한 값이 변이율보다 작을 경우 변이가 발생한다. 
변이율은 8개의 개체중 1개가 변이한다고 가정하여 1/8, 즉 0.125로 설정하여 변이율 보다 작을경우 부호가 변경되는 변이가 발생한다.
<pre><code>
  for(int i = 0; i < x.length; i++) {
            if(mupick< 0.125) { // 돌연변이율 8개중에 한개 , 즉 1/8 = 0.125
                arr[i] = x[i] *(-1); // 돌연변이율보다 작을 경우 돌연변이 발생, 부호 변경
                }
            else
                arr[i] = x[i];
              }
 </code></pre>
 
   
6. 선택, 교차, 변이의 유전과정을 끝낸 집합의 적합도를 판변한 뒤, **evaluation** 함수를 통해 이전 집합의 적합도의 최저값과 유전된 집합의 적합도의 최저값을 비교하여 적합도가 개선되지 않았다면 유전을 종료한다.
<pre><code>
          for(int i=0; i<a.length;i++){
            if(ftmin>a[i])
                ftmin = a[i];
            if(fftmin>x[i])
                fftmin = x[i];
        }

        int ftex=0;
        if(ftmin <= fftmin) // 유전한 적합도의 최저값이 이전과 작거나 같을 경우 유전 종료
            ftex = 1; // 유전 종료
        else
            ftex = 2; // 유전 계속

</code></pre>
 

### * 출력결과물
![출력 결과값](https://user-images.githubusercontent.com/63060298/85307782-4bc90180-b4eb-11ea-95c9-80446eb4b3ab.png)

출력 결과값은 위와 같고 이를 그래프로 나타낸것은 다음과 같다

![적합도 그래프](https://user-images.githubusercontent.com/63060298/85307581-086e9300-b4eb-11ea-8283-79d6ca28191d.png)

위 그림은 a값에 따른 적합도를 나타내며 **a = -1 일때, 적합도가 최저로 MSE의 관점에서 제일 적은 오차를 의미함으로 분산된 데이터의 회귀식의 기울기 값이 -1 (a = -1) 임을 예측할 수 있다** <br>
따라서, **회귀식은  y = -1x + 17** 이고 이를 산점도에 나타낸것은 다음과 같다

![회귀식](https://user-images.githubusercontent.com/63060298/85310091-8c764a00-b4ee-11ea-9ae9-58a695454001.png)



