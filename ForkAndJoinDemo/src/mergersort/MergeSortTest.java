package mergersort;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class MergeSortTest {

    public static void main(String[] args) {

        Random random = new Random();
        int[] nums = new int[1000000];
        for(int i = 0;i < nums.length;i++)
            nums[i] = random.nextInt(10000);
        ForkJoinPool pool = new ForkJoinPool();
        MergeSortTask task = new MergeSortTask(nums,0,nums.length);
        pool.execute(task);
        nums = task.join();

        boolean flag = true;
        for(int i = 0;i < nums.length-1;i++)
            if(nums[i+1] < nums[i]){flag = false;return;}
        System.out.println(flag?"排序成功!":"排序失败!");

    }

    private static class MergeSortTask extends RecursiveTask<int[]>
    {
        int[] nums;
        int begin,end;

        public MergeSortTask(int[] nums, int begin, int end) {
            this.nums = nums;
            this.begin = begin;
            this.end = end;
        }

        @Override
        protected int[] compute() {
            int sum = 0;
            if(end - begin < 300)
            {
                Arrays.sort(nums,begin,end);
            }
            else
            {
                int mid = (begin+end)/2;
                MergeSortTask task1 = new MergeSortTask(nums,begin,mid);
                MergeSortTask task2 = new MergeSortTask(nums,mid,end);
                task1.fork();
                task2.fork();
                int[]temp = new int[end - begin];
                int[] t1= task1.join();
                int[]t2 = task2.join();
                int i = begin,j = mid;
                int num = 0;
                for(;i < mid&&j <end;)
                {
                    if(t1[i] < t2[j])
                        temp[num++] = t1[i++];
                    else
                        temp[num++] = t2[j++];
                }
                while (i < mid)temp[num++] = t1[i++];
                while (j < end)temp[num++] = t1[j++];
                for(int k = 0;k < num;k++)
                    nums[begin+k] = temp[k];

            }


            return nums;
        }


    }





}
