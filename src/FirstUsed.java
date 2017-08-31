import java.util.Random;
import java.util.Scanner;

public class FirstUsed {
    public static void main(String[] args) 
    {
    	
    	int lastPageIn= 0; 
    	int frameCount = 0;
    	int pageFaultCount = 0;
    	int addressCount = 100;
    	int pageSize = 4096;
    	byte[] frames;
		byte[] pageNumbers = new byte[addressCount];
		boolean pageFaultCheck;

		// initialize input
		Scanner keyboard = new Scanner(System.in);
		
		//ask for and validate user input
		System.out.println("Enter number of frames (between 2 and 7)");
		frameCount = keyboard.nextInt();
		while(frameCount < 2 || frameCount > 7)
		{
			System.out.println("Must be between 2 and 7");
			frameCount = keyboard.nextInt();
		}

		// Initialize the array for frames
		// since max possible page number is 15, use 16 for validation
		frames = new byte[frameCount];
		for(int i = 0; i < frames.length; i++)
		{
			frames[i] = 16;
		}

		//track last page in for loop
		lastPageIn = frames.length - 1;

		// Generate a sequence of 100 random memory addresses from a 16-bit address space.
		// Use your address translation algorithm to determine address’s page.
		for(int i = 0; i < addressCount; i++)
		{
			Random randomNum = new Random();
			pageNumbers[i] = (byte) (randomNum.nextInt(Short.MAX_VALUE + 1) / pageSize);
			System.out.print(pageNumbers[i] + " - ");
		}
		System.out.print("\n");
		


		// Run through page number list, checking for page faults and assigning addresses, then output result
		for(int i = 0; i < pageNumbers.length; i++)
		{
			pageFaultCheck = true;
			for(int j = 0; j < frames.length; j++)
			{
				if(frames[j] == pageNumbers[i])
				{
					pageFaultCheck = false;
				}
			}
			if(pageFaultCheck)
			{
				if(lastPageIn == (frames.length - 1))
				{
					frames[0] = pageNumbers[i];
					lastPageIn = 0;
				}
				else
				{
					frames[lastPageIn + 1] = pageNumbers[i];
					lastPageIn++;
				}
				pageFaultCount++;
			}
			System.out.print((i + 1) +" => ");
			// console output
			// Display the progress of your algorithms and final results for the page-references string. 
			for(int j = 0; j < frames.length; j++)
			{
				if(frames[j] != 16) 
				{
					System.out.print("Frame " + j + ": " + frames[j]);
				}
				else
				{
					System.out.print(j + ":  ");
				}
				if( j != frames.length - 1)
				{
					System.out.print(" | ");
				}
			}
			System.out.print('\n');
		}
		System.out.println("Number of Page Faults: " + pageFaultCount);
		keyboard.close();
	}
}