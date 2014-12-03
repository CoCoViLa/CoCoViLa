grammar SpecificationLanguage;


options {
  language = Java;
}

import Java8;

@members{
	
}


metaInterfase
    :	'specification' Identifier (superMetaInterface)?
    	'{' specification '}'
	;
	
superMetaInterface
	:	'super' classType (',' classType)*
	;
	
specification
    :	( statement ';' )*
    ;
   
statement
    : variableDeclaration | variableAssignment | axiom
    | goal | aliasDeclaration | aliasDefinition | equation 
    ;
	
variableDeclaration
	:	variableModifier? type variableDeclarator (',' variableDeclarator)*
	;
	
variableModifier
	:	'static'	# staticVariable
	|	'const'		# constantVariable
	;

variableDeclarator
	:	Identifier ('=' variableInitializer)?						# variableDeclaratorInitializer
	|	Identifier ('=' variableAssigner)							# variableDeclaratorAssigner 
	|	Identifier ('(' specificationVariableDeclaration ')')?		# specificationVariable
	;

specificationVariableDeclaration
	:	specificationVariableDeclarator (',' specificationVariableDeclarator)*
	;

specificationVariableDeclarator
	:	Identifier '=' expression
	;

variableAssignment
	:	variableIdentifier '=' variableAssigner
	;
	
axiom
	:	( inputVariables = variableIdentifierList | subtaskList | (subtaskList ',' inputVariables = variableIdentifierList) )? '->' outputVariables = variableIdentifierList (',' exceptionList)? '{' method = Identifier '}'
	;
	//(method = Identifier | lambda = lambdaExpression)
subtask
	:	'[' (context = classType '|-')? inputVariables = variableIdentifierList '->' outputVariables = variableIdentifierList ']'
	;
	
subtaskList
	:	subtask (',' subtask)*
	;

exceptionList
	:	'(' classType ')' (',' '(' classType ')')*
	;
	
goal
	:	inputVariables = variableIdentifierList? '->' outputVariables = variableIdentifierList	
	;
	
aliasDeclaration
	:	'alias' ('(' type')')? Identifier ('=' aliasStructure)?
	;
	
aliasStructure
	:	('('|'[') ( variableAlias=variableIdentifierList | wildcardAliasName = wildcardAlias )(')'|']')
	;
	
wildcardAlias
	:	'*.' Identifier
	;

aliasDefinition
	:	variableIdentifier '=' aliasStructure
	;

type
    :   (classType | primitiveType) ('[' ']')*
    ;

classType
    :   Identifier ('.' Identifier )*
    ;

primitiveType
    :   'boolean'
    |   'char'
    |   'byte'
    |   'short'
    |   'int'
    |   'long'
    |   'float'
    |   'double'
    ;

equation
	:	left = expression '=' right = expression
	;

expression
	:	term
	|	'(' expression ')'
	|	'-' expression
	|	Identifier '(' expression ')'
	|	left = expression op=('*' | '/') right = expression
	|	left = expression op=('+' | '-') right = expression
    |   expression '^' expression
	;
	
term
    :	FloatingPointLiteral
    |	variableIdentifier
    ;

array
    :   '{' (inArrayVariableAssigner (',' inArrayVariableAssigner)* )? '}'
    ;
    
inArrayVariableAssigner
	:	variableAssigner | variableInitializer
	;

variableAssigner
    :   array
    |   'new' classType '(' expression (',' expression)* ')'
    |	StringLiteral
    |	'true'
    |	'false'
    ;
    
variableInitializer
    :   array
    |   expression
    ;

variableIdentifier
	:	Identifier (('.' Identifier | '*') )* ALIAS_ELEMENT_REF*  ('.' variableIdentifier)?
	;
	
variableIdentifierList
	:	variableIdentifier (',' variableIdentifier )*
	;

/*
NUMBER
    : INTEGER ('.' INTEGER)? (('e' | 'E') ('-'|'+')?INTEGER+)?
    ;
	
INTEGER
    : '0'..'9'+
    ;
*/

/*
STRING :	'"' (ESC|.)*? '"' ;
fragment ESC :	'\\"' | '\\\\' ;
*/

/*
Identifier
	:	LETTER LETTER_OR_DNUMBER*
	;
fragment LETTER : [a-zA-Z$_];
fragment LETTER_OR_DNUMBER : [a-zA-Z0-9$_];
*/

ALIAS_ELEMENT_REF
	:	'.' NUMBER+
	;

//WS : [ \t\r\n]+ -> skip ;
//COMMENT : '//' .*? '\r'? '\n' -> skip;
//BLOCK_COMMENT : '/*' .*? '*/' -> skip;

JAVA_BEFORE_SPEC : .*? '/*@' -> skip;
JAVA_AFTER_SPEC : '@*/' .*? -> skip;   	
