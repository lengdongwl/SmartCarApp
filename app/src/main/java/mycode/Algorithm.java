package mycode;

public class Algorithm {
    /* * * * * * * * * * * *
     * 判断一个数是否为质数，返回Boolean
     *
     * * * * * * * * * * * * * * * */
    public static boolean isPrime(int x) {
        boolean isPrime=true;
        if(x ==1 || x %2 ==0 && x !=2 ) {
            isPrime = false;
        }
        else {
            for( int i =3; i< Math.sqrt(x); i+=2) {
                if( x % i == 0) {
                    isPrime = false;
                    break;
                }
            }
        }
        return isPrime;
    }

    /* * * * * * * * * * * *
     * 判断两个数是否互质，返回ture或false
     *
     * * * * * * * * * * * * * * * */
    static boolean isCoprime(int x,int y)
    {
        if(x==1 && y==1)//1和1互质
            return true;
        else if(x<=0 || y<=0 || x==y)//非正整数都不存在互质的说法
            return false;
        else if(x==1 || y==1)//1和任何正整数都互质
            return true;
        else
        {
            int tmp=0;
            while(true)
            {
                tmp=x%y;
                if(tmp==0)
                {
                    break;
                }
                else
                {
                    x=y;
                    y=tmp;
                }
            }
            if(y==1)
                return true;
            else
                return false;

        }
    }
}
