package demo.thread;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * ForkJoin大数组拆分排序, 可以理解成并发情况下的递归任务计算
 * @author Administrator
 *
 */
public class ForkJoinTest extends RecursiveTask<int[]> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4512190240750465886L;
	private static int LEN = 100;
	private static int[] ARR = new int[LEN];
	
	
	static {
		Random r = new Random();
		for(int i=0; i<LEN; i++) {
			ARR[i] = r.nextInt(1000);
		}
	}
	
	public static void printArr(int[] arr) {
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<arr.length; i++) {
			if(i!=0) {
				sb.append(',');
			}
			sb.append(arr[i]);
		}
		System.out.println(sb);
	}
	
	private int[] sortArr;
	private Integer start;
	private Integer end;
	private static int THRESHOLD = 2;
	public ForkJoinTest(int[] arr, int start, int end) {
		this.sortArr = arr;
		this.start = start;
		this.end = end;
	}
	
	public static void main(String[] args) {
		ForkJoinTest.printArr(ARR);
		
		long t1 = System.currentTimeMillis();
		ForkJoinPool pool = new ForkJoinPool(4);
		ForkJoinTest task = new ForkJoinTest(ARR, 0, LEN);
		int[] result = pool.invoke(task);
		System.out.println(System.currentTimeMillis()-t1);
		ForkJoinTest.printArr(result);
	}

	@Override
	protected int[] compute() {
		int dex = this.end-this.start;
		//递归的临界值,在最小情况下的计算
		if(dex <= THRESHOLD) {
			if(dex>1 && this.sortArr[this.start]>this.sortArr[this.end-1]) {
				int tempVal = this.sortArr[start];
				this.sortArr[this.start] = this.sortArr[this.end-1];
				this.sortArr[this.end-1] = tempVal;
			}
			return this.sortArr;
			
		}
		
		int middle = (start + end)/2;
		ForkJoinTest task1 = new ForkJoinTest(Arrays.copyOfRange(this.sortArr, this.start, middle), 0, middle-this.start);
		ForkJoinTest task2 = new ForkJoinTest(Arrays.copyOfRange(this.sortArr, middle, this.end), 0, this.end-middle);
		//invokeAll的N个任务中，其中N-1个任务会使用fork()交给其它线程执行，但是，它还会留一个任务自己执行，这样，就充分利用了线程池，保证没有空闲的不干活的线程
		invokeAll(task1, task2);
		int[] arr1 = task1.join();
		int[] arr2 = task2.join();
		int index1 = 0;
		int index2 = 0;
		
		//递归结果汇总
		int[] result = new int[arr1.length+arr2.length];
		for(int i=0; i<result.length; i++) {
			int val;
			if(index1<arr1.length) {
				val = arr1[index1];
				if(index2<arr2.length) {
					if(val<arr2[index2]) {
						index1++;
					}else {
						val = arr2[index2];
						index2++;
					}
				}else {
					index1++;
				}
			}else {
				if(index2<arr2.length) {
					val = arr2[index2];
					index2++;
				}else {
					continue;
				}
			}
			result[i] = val;
		}
		
		return result;
	}

}
