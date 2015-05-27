specification TestSpec super Other{
final int a="12",b=12;
//vars
	//Simple
	int a;							//VALID, ok
	int a,b,c;						//VALID, ok
//	int a,b,;						//NOT VALID, ok
	int a=12;						//VALID, ok
	java.lang.Double a=12;			//VALID, ok
	int a=12,b,c;					//VALID, ok
	String str = "hello \"WORLD\"!";//VALID, ok
	int arr = {1,2,3};				//VALID, ok
	int arr = {"q","w","e"};		//VALID
	
	//Struct
//	SomeStruct a;					//VALID
//	SomeStruct a (a=1, b=2), b;		//VALID
//	SomeStruct a (a=1, b=), b;		//NOT VALID
	
	//Static
	static int a;					//VALID
	static int a,b,c;				//VALID
	static int a=12;				//VALID
	static java.lang.Double a=12;	//VALID
	static int a=12,b,c;			//VALID
	static String a = "asd";		//VALID
	static int a = {1,2,3};			//VALID
	
	//Final
//	const int a;					//NOT VALID
//	final int a,b,c;				//NOT VALID
	const int a=12;					//VALID
	const java.lang.Double a=12;	//VALID
//	const int a=12,b,c;				//NOT VALID
	const int a=12,b=13,c=14;		//VALID
	const String a = "asd";			//VALID
	const int a = {1,2,3};			//VALID
	
	//"Assignment"
	str = "new val";				//VALID
//	str = "new val;					//NOT VALID
//	arr[0] = 1;						//VALID
	spec.a = 1;						//VALID
	spec.innerSpce.a = 1;			//VALID
	a = 1.2;						//VALID
	a = sin(0.2);					//VALID
//	a = arr[1];						//VALID
//	a = ;							//NOT VALID

//Axiom
	//Simple
	a -> b {methodName};			//VALID
	a,b -> c {methodName};			//VALID
	a,b.x -> c {methodName};		//VALID
	-> c {methodName};				//VALID
	a -> b,(Exception){methodName};	//VALID
//	a -> {methodName};				//NOT VALID
	
	//Subtask
	[z->y],a -> b {methodName};		//VALID
	[z,x->y,u],a -> b {methodName};	//VALID
	[z->y] -> b.ou {methodName};	//VALID
//	[z[0]->y] -> b.ou {methodName};	//VALID
//	arr[0],arr[1] -> b {methodName};//VALID
	
	//Goal
	a,b,c->x,z;						//VALID
//	a[1],b,c->x,z;					//VALID
	->x,z;							//VALID
//	a,b,c->;						//NOT VALID
	
//Alias
	alias (int) myAlias = (a,b,c);	//VALID
	alias myAlias = (a,b,c);		//VALID
	alias myAlias = (*.c);			//VALID
	myAlias = [x,y,z];				//VALID
	
//Equation
	a = 1+2;						//VALID
	a = (b*2)/6;					//VALID
	a = (b*2.1^3)/6;				//VALID
	a = (b.e.a*2)/6;				//VALID
	a+c = (b*2.1^3)/6;				//VALID
//	a+arr[0] = (b*2.1^3)/6;			//VALID
}