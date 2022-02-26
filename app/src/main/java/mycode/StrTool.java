package mycode;

public class StrTool {
/* * * * * * * * * * * * * * * * *
 * 提取一段字符串中的数字
 * @param str待提取的字符串
 * @return 返回一个提取所有数字的数组
 * 示例：ExtractNumbers("09fs>546,901fhL_75")
 *      返回{0,9,546,901,75,}
 * * * * * * * * * * * * * * * * */

    static int[] ExtractNumbers(String s) {
        char[] data=s.toCharArray();
        int intArray[]=new int[50];
        int index=0;
        for(int i=0;i<data.length;i++) {
            if(data[i]=='0') {
                intArray[index++]=data[i]-48;
            }
            if(data[i]>'0'&&data[i]<='9') {
                intArray[index++]=data[i]-48;
                while((i+1)<data.length&&data[i+1]>='0'&&data[i+1]<='9') {
                    intArray[index-1]=intArray[index-1]*10+data[++i]-48;
                }
            }
        }
        int[] return_data=intArray;
        while(return_data.length>index) {
            if(return_data.length-index>=5) {
                return_data=new int[return_data.length-5];
            }
            else if(return_data.length-index>=2) {
                return_data=new int[return_data.length-2];
            }
            else{
                return_data=new int[return_data.length-1];
            }
        }
        for(int i=0;i<index;i++) {
            return_data[i]=intArray[i];
        }

        return return_data;
    }
    /* * * * * * * * * * * * * * * * *
     * 提取一段字符串中的数字
     * @param str待提取的字符串
     * @return 返回一个提取所有数字的数组
     * 示例：ExtractNumbers("09fs>546,901fhL_75")
     *      返回{0,9,5,4,6,9,0,1,7,5,}
     * * * * * * * * * * * * * * * * */

    static int[] ExtractNumbers2(String s) {
        char[] data=s.toCharArray();
        int intArray[]=new int[50];
        int index=0;
        for(int i=0;i<data.length;i++) {
            if(data[i]=='0') {
                intArray[index++]=data[i]-48;
            }
            if(data[i]>'0'&&data[i]<='9') {
                intArray[index++]=data[i]-48;
                if((i+1)<data.length&&data[i+1]>='0'&&data[i+1]<='9') {
                    intArray[index-1]=intArray[index-1]*10+data[++i]-48;
                }
            }
        }
        int[] return_data=intArray;
        while(return_data.length>index) {
            if(return_data.length-index>=5) {
                return_data=new int[return_data.length-5];
            }
            else if(return_data.length-index>=2) {
                return_data=new int[return_data.length-2];
            }
            else{
                return_data=new int[return_data.length-1];
            }
        }
        for(int i=0;i<index;i++) {
            return_data[i]=intArray[i];
        }

        return return_data;
    }



/* * * * * * * * * * * * * * * * *
 * 提取二维码中有效的路径值，即字符A-G,数字1-7
 * @param str待提取的字符串
 * @return 返回一个char[] 数组
 * 示例：Stringextraction("B46zBD4,c3.=+F6-)BD2B")
 *       返回[B, 4, D, 4, F, 6, D, 2]
 *
 * * * * * * * * * * * * * * * * */
    public static char[] Stringextraction(String str){
        char[] cArray=str.toCharArray();
        int flag=0;
        int marker=0;
        char[] data=new char[50];
        for(char c:cArray){
            if(flag==0){
                if(c>='A'&&c<='G'){
                    data[marker++]=c;
                    flag=1;
                }
            }
            if(flag==1){
                if(c>='A'&&c<='G'){
                    data[marker-1]=c;
                }
                if(c>='1'&&c<='7'){
                    data[marker++]=c;
                    flag=0;
                }
            }
        }
        if(marker>0&&data[marker-1]>='A'&&data[marker-1]<='G'){
            data[marker-1]=0;
            marker--;
        }
        return String.copyValueOf(data, 0, marker).toCharArray();
    }
}
