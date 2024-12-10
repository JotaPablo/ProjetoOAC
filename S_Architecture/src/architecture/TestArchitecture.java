package architecture;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;

import components.Memory;

public class TestArchitecture {
	
	//uncomment the anotation below to run the architecture showing components status
	//@Test
	public void testShowComponentes() {

		//a complete test (for visual purposes only).
		//a single code as follows
//		ldi 2
//		store 40
//		ldi -4
//		point:
//		store 41  //mem[41]=-4 (then -3, -2, -1, 0)
//		read 40
//		add 40    //mem[40] + mem[40]
//		store 40  //result must be in 40
//		read 41
//		inc
//		jn point
//		end
		
		Architecture arch = new Architecture(true);
		arch.getMemory().getDataList()[0]=7;
		arch.getMemory().getDataList()[1]=2;
		arch.getMemory().getDataList()[2]=6;
		arch.getMemory().getDataList()[3]=40;
		arch.getMemory().getDataList()[4]=7;
		arch.getMemory().getDataList()[5]=-4;
		arch.getMemory().getDataList()[6]=6;
		arch.getMemory().getDataList()[7]=41;
		arch.getMemory().getDataList()[8]=5;
		arch.getMemory().getDataList()[9]=40;
		arch.getMemory().getDataList()[10]=0;
		arch.getMemory().getDataList()[11]=40;
		arch.getMemory().getDataList()[12]=6;
		arch.getMemory().getDataList()[13]=40;
		arch.getMemory().getDataList()[14]=5;
		arch.getMemory().getDataList()[15]=41;
		arch.getMemory().getDataList()[16]=8;
		arch.getMemory().getDataList()[17]=4;
		arch.getMemory().getDataList()[18]=6;
		arch.getMemory().getDataList()[19]=-1;
		arch.getMemory().getDataList()[40]=0;
		arch.getMemory().getDataList()[41]=0;
		//now the program and the variables are stored. we can run
		arch.controlUnitEexec();
		
	}
	
	@Test
	public void testAddRegReg() {
	    Architecture arch = new Architecture();

	    // armazenando o número 0 na memória, na posição 31
	    arch.getMemory().getDataList()[31] = 0;
	    // armazenando o número 1 na memória, na posição 32
	    arch.getMemory().getDataList()[32] = 1;

	    // fazendo o PC apontar para a posição 30
	    arch.getExtbus1().put(30);
	    arch.getPC().store();

	    // agora configurando os valores dos registradores
	    arch.getIntbus1().put(1);
	    arch.getRegistersList().get(0).store(); // RPG0 tem 1
	    arch.getIntbus1().put(2);
	    arch.getRegistersList().get(1).store(); // RPG1 tem 2

	    // executando o comando move REG1 REG0.
	    arch.addRegReg();

	    // testando se o REG1 armazena 3 e o REG0 armazena 1:
	    arch.getRegistersList().get(0).read();
	    assertEquals(1, arch.getIntbus1().get());
	    arch.getRegistersList().get(1).read();
	    assertEquals(3, arch.getIntbus1().get());

	    // testando se o PC aponta para 3 posições após a original
	    // o PC estava apontando para 30; agora deve apontar para 33
	    arch.getPC().read();
	    assertEquals(33, arch.getExtbus1().get());

	    // os bits de flag 0 e 1 devem ser 0
	    assertEquals(0, arch.getFlags().getBit(0));
	    assertEquals(0, arch.getFlags().getBit(1));
	}

	@Test
	public void testAddMemReg() {
	    Architecture arch = new Architecture();

	    // armazenando o número 5 na memória, na posição 40
	    arch.getMemory().getDataList()[40] = 5;

	    // armazenando o número 40 na memória, na posição 11
	    arch.getMemory().getDataList()[11] = 40;
	    // armazenando o número 0 na memória, na posição 12
	    arch.getMemory().getDataList()[12] = 0;

	    // fazendo o PC apontar para a posição 10
	    arch.getExtbus1().put(10);
	    arch.getPC().store();

	    // agora configurando os valores do REG0
	    arch.getIntbus1().put(10);
	    arch.getRegistersList().get(0).store(); // RPG0 tem 10

	    // executando o comando move REG0.
	    arch.addMemReg();

	    // testando se REG0 armazena 15:
	    arch.getRegistersList().get(0).read();
	    assertEquals(15, arch.getIntbus1().get());

	    // testando se mem[40] armazena 5
	    arch.getExtbus1().put(40);
	    arch.getMemory().read();
	    assertEquals(5, arch.getExtbus1().get());

	    // testando se o PC aponta para 3 posições após a original
	    // o PC estava apontando para 10; agora deve apontar para 13
	    arch.getPC().read();
	    assertEquals(13, arch.getExtbus1().get());

	    // os bits de flag 0 e 1 devem ser 0
	    assertEquals(0, arch.getFlags().getBit(0));
	    assertEquals(0, arch.getFlags().getBit(1));
	}

	@Test
	public void testAddRegMem() {
	    Architecture arch = new Architecture();

	    // armazenando o número 5 na memória, na posição 40
	    arch.getMemory().getDataList()[40] = 5;

	    // armazenando o número 0 na memória, na posição 11
	    arch.getMemory().getDataList()[11] = 0;
	    // armazenando o número 40 na memória, na posição 12
	    arch.getMemory().getDataList()[12] = 40;

	    // fazendo o PC apontar para a posição 10
	    arch.getExtbus1().put(10);
	    arch.getPC().store();

	    // agora configurando os valores do REG0
	    arch.getIntbus1().put(10);
	    arch.getRegistersList().get(0).store(); // RPG0 tem 1

	    // executando o comando move REG0.
	    arch.addRegMem();

	    // testando se REG0 armazena 10:
	    arch.getRegistersList().get(0).read();
	    assertEquals(10, arch.getIntbus1().get());

	    // testando se mem[40] armazena 15
	    arch.getExtbus1().put(40);
	    arch.getMemory().read();
	    assertEquals(15, arch.getExtbus1().get());

	    // testando se o PC aponta para 3 posições após a original
	    // o PC estava apontando para 10; agora deve apontar para 13
	    arch.getPC().read();
	    assertEquals(13, arch.getExtbus1().get());

	    // os bits de flag 0 e 1 devem ser 0
	    assertEquals(0, arch.getFlags().getBit(0));
	    assertEquals(0, arch.getFlags().getBit(1));
	}

	@Test
	public void testAddImmReg() {
	    Architecture arch = new Architecture();

	    // armazenando o número 40 na memória, na posição 11
	    arch.getMemory().getDataList()[11] = 5;
	    // armazenando o número 0 na memória, na posição 12
	    arch.getMemory().getDataList()[12] = 0;

	    // fazendo o PC apontar para a posição 10
	    arch.getExtbus1().put(10);
	    arch.getPC().store();

	    // agora configurando os valores do REG0
	    arch.getIntbus1().put(10);
	    arch.getRegistersList().get(0).store(); // RPG0 tem 10

	    // executando o comando move REG0.
	    arch.addImmReg();

	    // testando se REG0 armazena 15:
	    arch.getRegistersList().get(0).read();
	    assertEquals(15, arch.getIntbus1().get());

	    // testando se o PC aponta para 3 posições após a original
	    // o PC estava apontando para 10; agora deve apontar para 13
	    arch.getPC().read();
	    assertEquals(13, arch.getExtbus1().get());

	    // os bits de flag 0 e 1 devem ser 0
	    assertEquals(0, arch.getFlags().getBit(0));
	    assertEquals(0, arch.getFlags().getBit(1));
	}

	
	@Test
	public void testSubRegReg() {
	    Architecture arch = new Architecture();

	    // armazenando o número 0 na memória, na posição 31
	    arch.getMemory().getDataList()[31] = 0;
	    // armazenando o número 1 na memória, na posição 32
	    arch.getMemory().getDataList()[32] = 1;

	    // fazendo o PC apontar para a posição 30
	    arch.getExtbus1().put(30);
	    arch.getPC().store();

	    // agora configurando os valores dos registradores
	    arch.getIntbus1().put(1);
	    arch.getRegistersList().get(0).store(); // RPG0 tem 1
	    arch.getIntbus1().put(2);
	    arch.getRegistersList().get(1).store(); // RPG1 tem 2

	    // executando o comando move REG1 REG0.
	    arch.subRegReg();

	    // testando se o REG1 armazena -1 e o REG0 armazena 1:
	    arch.getRegistersList().get(0).read();
	    assertEquals(1, arch.getIntbus1().get());
	    arch.getRegistersList().get(1).read();
	    assertEquals(-1, arch.getIntbus1().get());

	    // testando se o PC aponta para 3 posições após a original
	    // o PC estava apontando para 30; agora deve apontar para 33
	    arch.getPC().read();
	    assertEquals(33, arch.getExtbus1().get());

	    // os bits de flag 0 e 1 devem ser 0 e 1
	    assertEquals(0, arch.getFlags().getBit(0));
	    assertEquals(1, arch.getFlags().getBit(1));
	}

	@Test
	public void testSubMemReg() {
	    Architecture arch = new Architecture();

	    // armazenando o número 5 na memória, na posição 40
	    arch.getMemory().getDataList()[40] = 5;

	    // armazenando o número 40 na memória, na posição 11
	    arch.getMemory().getDataList()[11] = 40;
	    // armazenando o número 0 na memória, na posição 12
	    arch.getMemory().getDataList()[12] = 0;

	    // fazendo o PC apontar para a posição 10
	    arch.getExtbus1().put(10);
	    arch.getPC().store();

	    // agora configurando os valores do REG0
	    arch.getIntbus1().put(10);
	    arch.getRegistersList().get(0).store(); // RPG0 tem 10

	    // executando o comando move REG0.
	    arch.subMemReg();

	    // testando se REG0 armazena 15:
	    arch.getRegistersList().get(0).read();
	    assertEquals(-5, arch.getIntbus1().get());

	    // testando se mem[40] armazena 5
	    arch.getExtbus1().put(40);
	    arch.getMemory().read();
	    assertEquals(5, arch.getExtbus1().get());

	    // testando se o PC aponta para 3 posições após a original
	    // o PC estava apontando para 10; agora deve apontar para 13
	    arch.getPC().read();
	    assertEquals(13, arch.getExtbus1().get());

	    // os bits de flag 0 e 1 devem ser 0 e 1
	    assertEquals(0, arch.getFlags().getBit(0));
	    assertEquals(1, arch.getFlags().getBit(1));
	}

	@Test
	public void testSubRegMem() {
	    Architecture arch = new Architecture();

	    // armazenando o número 5 na memória, na posição 40
	    arch.getMemory().getDataList()[40] = 5;

	    // armazenando o número 0 na memória, na posição 11
	    arch.getMemory().getDataList()[11] = 0;
	    // armazenando o número 40 na memória, na posição 12
	    arch.getMemory().getDataList()[12] = 40;

	    // fazendo o PC apontar para a posição 10
	    arch.getExtbus1().put(10);
	    arch.getPC().store();

	    // agora configurando os valores do REG0
	    arch.getIntbus1().put(10);
	    arch.getRegistersList().get(0).store(); // RPG0 tem 1

	    // executando o comando move REG0.
	    arch.subRegMem();

	    // testando se REG0 armazena 10:
	    arch.getRegistersList().get(0).read();
	    assertEquals(10, arch.getIntbus1().get());

	    // testando se mem[40] armazena 5
	    arch.getExtbus1().put(40);
	    arch.getMemory().read();
	    assertEquals(5, arch.getExtbus1().get());

	    // testando se o PC aponta para 3 posições após a original
	    // o PC estava apontando para 10; agora deve apontar para 13
	    arch.getPC().read();
	    assertEquals(13, arch.getExtbus1().get());

	    // os bits de flag 0 e 1 devem ser 0
	    assertEquals(0, arch.getFlags().getBit(0));
	    assertEquals(0, arch.getFlags().getBit(1));
	}

	@Test
	public void testSubImmReg() {
	    Architecture arch = new Architecture();

	    // armazenando o número 5 na memória, na posição 11
	    arch.getMemory().getDataList()[11] = 5;
	    // armazenando o número 0 na memória, na posição 12
	    arch.getMemory().getDataList()[12] = 0;

	    // fazendo o PC apontar para a posição 10
	    arch.getExtbus1().put(10);
	    arch.getPC().store();

	    // agora configurando os valores do REG0
	    arch.getIntbus1().put(10);
	    arch.getRegistersList().get(0).store(); // RPG0 tem 10

	    // executando o comando move REG0.
	    arch.subImmReg();

	    // testando se REG0 armazena -5:
	    arch.getRegistersList().get(0).read();
	    assertEquals(-5, arch.getIntbus1().get());

	    // testando se o PC aponta para 3 posições após a original
	    // o PC estava apontando para 10; agora deve apontar para 13
	    arch.getPC().read();
	    assertEquals(13, arch.getExtbus1().get());

	    // os bits de flag 0 e 1 devem ser 0 e 1
	    assertEquals(0, arch.getFlags().getBit(0));
	    assertEquals(1, arch.getFlags().getBit(1));
	}
	
	
	@Test
	public void testMoveMemReg() {
		Architecture arch = new Architecture();

		// Armazenando o número 1 na memória, na posição 31
		arch.getMemory().getDataList()[31] = 37;
		// Armazenando o número 0 na memória, na posição 32
		arch.getMemory().getDataList()[32] = 0;
		// Armazenando o número 1 na posição 37 da memória
		arch.getMemory().getDataList()[37] = 1;
		// Fazendo o PC apontar para a posição 30
		arch.getExtbus1().put(30);
		arch.getPC().store();

		// Agora configurando os valores dos registradores
		arch.getIntbus1().put(45);
		arch.getRegistersList().get(0).store(); // RPG0 possui 45
		System.out.println(""+arch.getRegistersList().get(0).getData());

		// Executando o comando move mem reg0
		arch.moveMemReg();
		System.out.println(""+arch.getRegistersList().get(0).getData());


		// Testando se tanto REG0 ta armazenando 1
		arch.getRegistersList().get(0).read();
		assertEquals(1, arch.getIntbus1().get());

		// Testando se o PC aponta para 3 posições após o original
		// O PC estava apontando para 30; agora deve estar apontando para 33
		arch.getPC().read();
		assertEquals(33, arch.getExtbus1().get());
	}

	@Test
	public void testMoveRegMem() {
		Architecture arch = new Architecture();

		// Armazenando o número 1 na memória, na posição 31
		arch.getMemory().getDataList()[31] = 0;
		// Armazenando o número 0 na memória, na posição 32
		arch.getMemory().getDataList()[32] = 37;
		// Armazenando o número 1 na posição 37 da memória
		arch.getMemory().getDataList()[37] = 15;
		// Fazendo o PC apontar para a posição 30
		arch.getExtbus1().put(30);
		arch.getPC().store();

		// Agora configurando os valores dos registradores
		arch.getIntbus1().put(1);
		arch.getRegistersList().get(0).store(); // RPG0 possui 1

		// Executando o comando move mem reg0
		arch.moveRegMem();

		// Testando se mem[37] ta armazenando 1
		arch.getExtbus1().put(37);
	    arch.getMemory().read();
	    assertEquals(1, arch.getExtbus1().get());

		// Testando se o PC aponta para 3 posições após o original
		// O PC estava apontando para 30; agora deve estar apontando para 33
		arch.getPC().read();
		assertEquals(33, arch.getExtbus1().get());
	}
	
	@Test
	public void testMoveRegReg() {
		Architecture arch = new Architecture();

		// Armazenando na pos31 da memória 1 (regA)
		arch.getMemory().getDataList()[31] = 1;
		// Armazenando na pos32 da memória 0 (regB)
		arch.getMemory().getDataList()[32] = 0;
		// Fazendo o PC apontar para a posição 30
		arch.getExtbus1().put(30);
		arch.getPC().store();

		// Agora configurando os valores dos registradores
		arch.getIntbus1().put(30);
		arch.getRegistersList().get(0).store(); // RPG0 possui 30
		arch.getIntbus1().put(10);
		arch.getRegistersList().get(1).store(); // RPG0 possui 10

		// Executando o comando move reg
		arch.moveRegReg();

		// Testando se tanto reg1 e reg0 ta armazenando 10
		arch.getRegistersList().get(0).read();
		assertEquals(10, arch.getIntbus1().get());
		arch.getRegistersList().get(1).read();
		assertEquals(10, arch.getIntbus1().get());

		// Testando se o PC aponta para 3 posições após o original
		// O PC estava apontando para 30; agora deve estar apontando para 33
		arch.getPC().read();
		assertEquals(33, arch.getExtbus1().get());
	}
	
	@Test
	public void testMoveImmReg() {
		Architecture arch = new Architecture();

		// Armazenando o número 1 na memória, na posição 31
		arch.getMemory().getDataList()[31] = 1;
		// Armazenando o número 0 na memória, na posição 32
		arch.getMemory().getDataList()[32] = 0;
		// Fazendo o PC apontar para a posição 30
		arch.getExtbus1().put(30);
		arch.getPC().store();

		// Agora configurando os valores dos registradores
		arch.getIntbus1().put(31);
		arch.getRegistersList().get(0).store(); // RPG0 possui 31

		// Executando o comando move mem reg0
		arch.moveImmReg();

		// Testando se mem[37] ta armazenando 1
		arch.getExtbus1().put(31);
	    arch.getMemory().read();
	    assertEquals(1, arch.getExtbus1().get());
	    
	    //Testando se REG0 armazena 1:
	    arch.getRegistersList().get(0).read();
		assertEquals(1, arch.getIntbus1().get());
	    

		// Testando se o PC aponta para 3 posições após o original
		// O PC estava apontando para 30; agora deve estar apontando para 33
		arch.getPC().read();
		assertEquals(33, arch.getExtbus1().get());
	}
	
	@Test
	public void testInc() {
		
		Architecture arch = new Architecture();

		// Armazenando na pos31 da memória 0 que e o reg
		arch.getMemory().getDataList()[31] = 0;

		// Fazendo o PC apontar para a posição 30
		arch.getExtbus1().put(30);
		arch.getPC().store();

		// Agora configurando os valores dos registradores
		arch.getIntbus1().put(3);
		arch.getRegistersList().get(0).store(); // RPG0 possui 3

		// Executando o comando move reg
		arch.inc();

		// Testando se o reg0 incrementou
		arch.getRegistersList().get(0).read();
		assertEquals(4, arch.getIntbus1().get());

		// Testando se o PC aponta para 2 posições após o original
		// O PC estava apontando para 30; agora deve estar apontando para 32
		arch.getPC().read();
		assertEquals(32, arch.getExtbus1().get());
	}
	
	@Test
	public void testJmp() {
		Architecture arch = new Architecture();
		//storing the number 10 in PC
		arch.getIntbus2().put(10);
		arch.getPC().internalStore();

		//storing the number 17 in the memory, in the position just before that one adressed by PC
		arch.getExtbus1().put(11); //the position is 11, once PC points to 10
		arch.getMemory().store();
		arch.getExtbus1().put(15);
		arch.getMemory().store();

        arch.getExtbus1().put(15); //the position is 11, once PC points to 10
		arch.getMemory().store();
		arch.getExtbus1().put(17);
		arch.getMemory().store();
		
		
		//testing if PC stores the number 10
		arch.getPC().read();
		assertEquals(10, arch.getExtbus1().get());
		
		//now we can perform the jmpMem method. 
		//we will move the the number 17 (stored in the 11th position in the memory) 
		//into the PC
		arch.jmp();
		arch.getPC().internalRead();;
		//the internalbus2 must contains the number 17
		assertEquals(17, arch.getIntbus2().get());

	}
	
	@Test
	public void testJz() {
		Architecture arch = new Architecture();
		
		//storing the number 10 in PC
		arch.getIntbus2().put(10);
		arch.getPC().internalStore();
		
		//storing the number 17 in the into the memory, in position 11, the position just after PC
		arch.getExtbus1().put(11);
		arch.getMemory().store();
		arch.getExtbus1().put(15);
		arch.getMemory().store();

        arch.getExtbus1().put(15);
		arch.getMemory().store();
		arch.getExtbus1().put(17);
		arch.getMemory().store();

		//now we can perform the jzMem method. 

		//CASE 1.
		//Bit ZERO is equals to 1
		arch.getFlags().setBit(0, 1);
		
		//So, we will move the the number 17 (stored in the 31th position in the memory) 
		//into the PC

		//testing if PC stores the number 10
		arch.getPC().read();
		assertEquals(10, arch.getExtbus1().get());		

		arch.jz();
		
		//PC must be storing the number 17
		arch.getPC().internalRead();
		assertEquals(17, arch.getIntbus2().get());
		
		//CASE 2.
		//Bit ZERO is equals to 0
		arch.getFlags().setBit(0, 0);
		//PC must have the number 10 initially (in this time, by using the external bus)
		arch.getExtbus1().put(10);
		arch.getPC().store();
		//destroying the data in external bus
		arch.getExtbus1().put(0);

		//testing if PC stores the number 10
		arch.getPC().read();
		assertEquals(10, arch.getExtbus1().get());	
		
		//Note that the memory was not changed. So, in position 11 we have the number 17
		
		//Once the ZERO bit is 0, we WILL NOT move the number 17 (stored in the 11th position in the memory)
		//into the PC.
		//The original PC position was 10. The parameter is in position 11. So, now PC must be pointing to 12
		arch.jz();
		//PC contains the number 12
		arch.getPC().internalRead();
		assertEquals(12, arch.getIntbus2().get());
	}
	
	@Test
	public void testJn() {
		Architecture arch = new Architecture();
		
		//storing the number 30 in PC
		arch.getIntbus2().put(10);
		arch.getPC().internalStore();
		
		//storing the number 25 in the into the memory, in position 31, the position just after PC
		arch.getExtbus1().put(11);
		arch.getMemory().store();
		arch.getExtbus1().put(15);
		arch.getMemory().store();
		
		arch.getExtbus1().put(15);
		arch.getMemory().store();
		arch.getExtbus1().put(17);
		arch.getMemory().store();

		//now we can perform the jnMem method. 

		//CASE 1.
		//Bit NEGATIVE is equals to 1
		arch.getFlags().setBit(1, 1);
		
		//So, we will move the the number 17 (stored in the 31th position in the memory) 
		//into the PC

		//testing if PC stores the number 10
		arch.getPC().read();
		assertEquals(10, arch.getExtbus1().get());		

		arch.jn();
		
		//PC must be storng the number 17
		arch.getPC().internalRead();
		assertEquals(17, arch.getIntbus2().get());
		
		//CASE 2.
		//Bit NEGATIVE is equals to 0
		arch.getFlags().setBit(1, 0);
		//PC must have the number 17 initially (in this time, by using the external bus)
		arch.getExtbus1().put(10);
		arch.getPC().store();
		//destroying the data in external bus
		arch.getExtbus1().put(0);

		//testing if PC stores the number 17
		arch.getPC().read();
		assertEquals(10, arch.getExtbus1().get());	
		
		//Note that the memory was not changed. So, in position 11 we have the number 17
		
		//Once the ZERO bit is 0, we WILL NOT move the number 17 (stored in the 31th position in the memory)
		//into the PC.
		//The original PC position was 10. The parameter is in position 11. So, now PC must be pointing to 12
		arch.jn();
		//PC contains the number 12
		arch.getPC().internalRead();
		assertEquals(12, arch.getIntbus2().get());
	}
	
    @Test
    public void testeJeq(){
        Architecture arch = new Architecture();
        //storing the number 1 in the memory, in position 11
		arch.getMemory().getDataList()[11]=0;
		//storing the number 0 in the memory, in position 12
		arch.getMemory().getDataList()[12]=1;
		//making PC points to position 10
        //storing the number 10 in PC
		arch.getIntbus2().put(10);
		arch.getPC().internalStore();
		
		
        //storing the number 15 in the into the memory, in position 11, the position just after PC
		arch.getExtbus1().put(13);
		arch.getMemory().store();
		arch.getExtbus1().put(15);
		arch.getMemory().store();
		
		arch.getExtbus1().put(15);
		arch.getMemory().store();
		arch.getExtbus1().put(17);
		arch.getMemory().store();
        
        // caso não igual

         //now setting the registers values
		arch.getIntbus1().put(45);
		arch.getRegistersList().get(0).store(); //RPG0 has 45
		arch.getIntbus1().put(99);
		arch.getRegistersList().get(1).store(); //RPG1 has 99
        //testing if PC stores the number 10
		arch.getPC().read();
		assertEquals(10, arch.getExtbus1().get());
        arch.jeq();
            
        //PC must be storng the number 14
		arch.getPC().internalRead();
		assertEquals(14, arch.getIntbus2().get());

        // caso igual
		arch.getIntbus2().put(10);
		arch.getPC().internalStore();
		
        arch.getIntbus1().put(45);
		arch.getRegistersList().get(0).store(); //RPG0 has 45
		arch.getRegistersList().get(1).store(); //RPG1 has 45

        arch.getPC().read();
		assertEquals(10, arch.getExtbus1().get());
        arch.jeq();
        
        //PC must be storng the number 17
		arch.getPC().internalRead();
		assertEquals(17, arch.getIntbus2().get());

     }
	
	@Test
	public void testJneq() {
		Architecture arch = new Architecture(); //jneq %<regA> %<regB> <mem>   || se RegA!=RegB então PC <- mem (desvio condicional)

		// Setup memory values
		arch.getMemory().getDataList()[31] = 0;  // First register ID
		arch.getMemory().getDataList()[32] = 1;  // Second register ID
		arch.getMemory().getDataList()[33] = 100;  // Jump address
	
		// Initialize PC to position 30
		arch.getExtbus1().put(30);
		arch.getPC().store();
	
		// Test Case 1: Different Values (Should Jump)
		// Set registers with different values
		arch.getIntbus1().put(1);
		arch.getRegistersList().get(0).store(); // RPG0 = 1
		arch.getIntbus1().put(2);
		arch.getRegistersList().get(1).store(); // RPG1 = 2
	
		arch.jneq();
	
		// Verify registers maintain their values
		arch.getRegistersList().get(0).read();
		assertEquals(1, arch.getIntbus1().get());
		arch.getRegistersList().get(1).read();
		assertEquals(2, arch.getIntbus1().get());
	
		// Verify PC jumped to address 100
		arch.getPC().read();
		assertEquals(100, arch.getExtbus1().get());
	
		// Test Case 2: Equal Values (Should Not Jump)
		// Reset PC
		arch.getExtbus1().put(30);
		arch.getPC().store();
	
		// Set registers with same value
		arch.getIntbus1().put(5);
		arch.getRegistersList().get(0).store(); // RPG0 = 5
		arch.getRegistersList().get(1).store(); // RPG1 = 5
	
		arch.jneq();
	
		// Verify registers maintain their values
		arch.getRegistersList().get(0).read();
		assertEquals(5, arch.getIntbus1().get());
		arch.getRegistersList().get(1).read();
		assertEquals(5, arch.getIntbus1().get());
	
		// Verify PC points to next instruction (34) instead of jumping
		arch.getPC().read();
		assertEquals(34, arch.getExtbus1().get());
	
		// Verify flags
		assertEquals(1, arch.getFlags().getBit(0)); // Zero flag should be set for equal values
		assertEquals(0, arch.getFlags().getBit(1)); // Negative flag should be clear
	}
	
	@Test
	public void testJgt() {
		Architecture arch = new Architecture();

		// Setup memory values
		arch.getMemory().getDataList()[31] = 0;  // First register ID
		arch.getMemory().getDataList()[32] = 1;  // Second register ID
		arch.getMemory().getDataList()[33] = 100;  // Jump address

		// Initialize PC to position 30
		arch.getExtbus1().put(30);
		arch.getPC().store();

		// Test Case 1: RegA < RegB (Should not Jump)
		arch.getIntbus1().put(1);
		arch.getRegistersList().get(0).store(); // RPG0 = 1
		arch.getIntbus1().put(2); 
		arch.getRegistersList().get(1).store(); // RPG1 = 2

		arch.jgt();

		// Verify registers maintain their values
		arch.getRegistersList().get(0).read();
		assertEquals(1, arch.getIntbus1().get());
		arch.getRegistersList().get(1).read();
		assertEquals(2, arch.getIntbus1().get());

		// Verify PC points to next instruction (34) instead of jumping

		// Verify PC jumped to address 34
		arch.getPC().read();
		assertEquals(34, arch.getExtbus1().get());

		// Test Case 2: RegA > RegB (Should Jump)
		arch.getExtbus1().put(30);
		arch.getPC().store();

		arch.getIntbus1().put(2);
		arch.getRegistersList().get(0).store(); // RPG0 = 2
		arch.getIntbus1().put(1);
		arch.getRegistersList().get(1).store(); // RPG1 = 1

		arch.jgt();

		// Verify PC jumped to address 100
		arch.getPC().read();
		assertEquals(100, arch.getExtbus1().get());

		// Test Case 3: RegA = RegB (Should Not Jump) 
		arch.getExtbus1().put(30);
		arch.getPC().store();

		arch.getIntbus1().put(5);
		arch.getRegistersList().get(0).store(); // RPG0 = 5
		arch.getRegistersList().get(1).store(); // RPG1 = 5

		arch.jgt();

		// Verify PC points to next instruction (34)
		arch.getPC().read(); 
		assertEquals(34, arch.getExtbus1().get());
	}
	
	@Test
	public void testJlw() {
		Architecture arch = new Architecture();

		// Setup memory values
		arch.getMemory().getDataList()[31] = 0;  // First register ID
		arch.getMemory().getDataList()[32] = 1;  // Second register ID
		arch.getMemory().getDataList()[33] = 100;  // Jump address

		// Initialize PC to position 30
		arch.getExtbus1().put(30);
		arch.getPC().store();

		// Test Case 1: RegA < RegB (Should Jump)
		arch.getIntbus1().put(1);
		arch.getRegistersList().get(0).store(); // RPG0 = 1
		arch.getIntbus1().put(2);
		arch.getRegistersList().get(1).store(); // RPG1 = 2

		arch.jlw();

		// Verify registers maintain their values
		arch.getRegistersList().get(0).read();
		assertEquals(1, arch.getIntbus1().get());
		arch.getRegistersList().get(1).read();
		assertEquals(2, arch.getIntbus1().get());

		// Verify PC jumped to address 100
		arch.getPC().read();
		assertEquals(100, arch.getExtbus1().get());

		// Test Case 2: RegA > RegB (Should Not Jump)
		arch.getExtbus1().put(30);
		arch.getPC().store();

		arch.getIntbus1().put(7);
		arch.getRegistersList().get(0).store(); // RPG0 = 7
		arch.getIntbus1().put(3);
		arch.getRegistersList().get(1).store(); // RPG1 = 3

		arch.jlw();

		// Verify PC points to next instruction (34) instead of jumping
		arch.getPC().read();
		assertEquals(34, arch.getExtbus1().get());

		// Test Case 3: RegA = RegB (Should Not Jump)
		arch.getExtbus1().put(30);
		arch.getPC().store();

		arch.getIntbus1().put(5);
		arch.getRegistersList().get(0).store(); // RPG0 = 5
		arch.getRegistersList().get(1).store(); // RPG1 = 5

		arch.jlw();

		// Verify PC points to next instruction (34)
		arch.getPC().read();
		assertEquals(34, arch.getExtbus1().get());
	}
	
	
	@Test
	public void  testCall() {
		
		Architecture arch = new Architecture();

		
	// Setup memory values
		arch.getMemory().getDataList()[31] = 100;  // call address
			
	// Initialize PC to position 30
		arch.getExtbus1().put(30);
		arch.getPC().store();

	
	//Initialize stkTop(4) and stkBot(5) value in 125
		arch.getExtbus1().put(125);
		arch.getRegistersList().get(4).store();
		arch.getRegistersList().get(5).store();
	
	arch.call();
	
	// Verify PC called to address 100
		arch.getPC().read();
		assertEquals(100, arch.getExtbus1().get());
	// Verify if StkBot remains 125
		 arch.getRegistersList().get(5).read();
		 assertEquals(125, arch.getExtbus1().get());
	// Verify if StkTOP is 124
		 arch.getRegistersList().get(4).read();
		 assertEquals(124, arch.getExtbus1().get());
			
	//Verify if 32 is in memory[125]
	arch.getExtbus1().put(125);
    arch.getMemory().read();
    assertEquals(32, arch.getExtbus1().get());
			
	}
	
	@Test
	public void testRet() {
		
		Architecture arch = new Architecture();

		
		// Setup memory values
			arch.getMemory().getDataList()[125] = 32;  // call address
			
		//Initialize stkTop(4) value in 124 and stkBot(5) value in 125
			arch.getExtbus1().put(124);
			arch.getRegistersList().get(4).store();
			arch.getExtbus1().put(125);
			arch.getRegistersList().get(5).store();
		
		// Initialize PC to position 100
			arch.getExtbus1().put(100);
			arch.getPC().store();
		
		arch.ret();
		
		// Verify PC called to address 32
				arch.getPC().read();
		// Verify if StkBot remains 125
				arch.getRegistersList().get(5).read();
		// Verify if StkTOP is 125
				 arch.getRegistersList().get(4).read();
				 assertEquals(124, arch.getExtbus1().get());
	}
			
			
		
	
	@Test
	public void testFillCommandsList() {
		
		// Todas as intruções devem estar na CommandList
		// Tabela de instruções, com base nos microprogramas especificados
		/*
		 * add %<regA> %<regB>        -> RegB <- RegA + RegB
		 * add <addr> %<regA>         -> rpg <- rpg + addr
		 * add %<regA> <addr>         -> addr <- rpg + regA
		 * add <imm> %<regA>          -> rpg <- regA + imm
		 * sub <addr>                 -> rpg <- rpg - addr
		 * jmp <addr>                 -> pc <- addr
		 * jz <addr>                  -> se bitZero, pc <- addr
		 * jn <addr>                  -> se bitneg, pc <- addr
		 * read <addr>                -> rpg <- addr
		 * store <addr>               -> addr <- rpg
		 * ldi <x>                    -> rpg <- x
		 * inc                        -> rpg++
		 * move %<regA> %<regB>       -> regA <- regB
		 */

		
		
		Architecture arch = new Architecture();
		ArrayList<String> commands = arch.getCommandsList();

		assertTrue("addRegReg".equals(commands.get(0)));  // 0
		assertTrue("addMemReg".equals(commands.get(1)));  // 1
		assertTrue("addRegMem".equals(commands.get(2)));  // 2
		assertTrue("addImmReg".equals(commands.get(3)));  // 3
		assertTrue("subRegReg".equals(commands.get(4)));  // 4
		assertTrue("subMemReg".equals(commands.get(5)));  // 5
		assertTrue("subRegMem".equals(commands.get(6)));  // 6
		assertTrue("subImmReg".equals(commands.get(7)));  // 7
		assertTrue("moveMemReg".equals(commands.get(8))); // 8
		assertTrue("moveRegMem".equals(commands.get(9))); // 9
		assertTrue("moveRegReg".equals(commands.get(10))); // 10
		assertTrue("moveImmReg".equals(commands.get(11))); // 11
		assertTrue("inc".equals(commands.get(12)));       // 12
		assertTrue("jmp".equals(commands.get(13)));        // 13
		assertTrue("jn".equals(commands.get(14)));         // 14
		assertTrue("jz".equals(commands.get(15)));         // 15
		assertTrue("jeq".equals(commands.get(16)));         // 16
		assertTrue("jneq".equals(commands.get(17)));         // 17
		assertTrue("jgt".equals(commands.get(18)));         // 18
		assertTrue("jlw".equals(commands.get(19)));         // 19
		assertTrue("call".equals(commands.get(20)));         // 20
		assertTrue("ret".equals(commands.get(21)));         // 21
	}
	
	@Test
	public void testReadExec() throws IOException {
		Architecture arch = new Architecture();
		arch.readExec("testFile");
		assertEquals(5, arch.getMemory().getDataList()[0]);
		assertEquals(4, arch.getMemory().getDataList()[1]);
		assertEquals(3, arch.getMemory().getDataList()[2]);
		assertEquals(2, arch.getMemory().getDataList()[3]);
		assertEquals(1, arch.getMemory().getDataList()[4]);
		assertEquals(0, arch.getMemory().getDataList()[5]);
	}

}
