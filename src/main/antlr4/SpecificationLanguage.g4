grammar SpecificationLanguage;


options {
  language = Java;
}

import Java8Lambdas;

@members{
	
}


metaInterface
    :	'specification' Identifier (superMetaInterface)?
    	'{' specification '}'
	;
	
superMetaInterface
	:	'super' classOrInterfaceType (',' classOrInterfaceType)*
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
	:	( inputVariables = variableIdentifierList | subtaskList | (subtaskList ',' inputVariables = variableIdentifierList) )? '->' outputVariables = variableIdentifierList (',' exceptionList)? '{' (method = Identifier | lambda = lambdaExpression) '}'
	;

subtask
	:	'[' (context = classOrInterfaceType '|-')? inputVariables = variableIdentifierList '->' outputVariables = variableIdentifierList ']'
	;
	
subtaskList
	:	subtask (',' subtask)*
	;

exceptionList
	:	'(' classOrInterfaceType ')' (',' '(' classOrInterfaceType ')')*
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
	:	classOrInterfaceType ('[' ']')*
	|	primitiveType ('[' ']')*
	|	'void'
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
    :	literal
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
    |   'new' classOrInterfaceType '(' expression (',' expression)* ')'
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
	:	'.' DecimalIntegerLiteral+
	;

//WS : [ \t\r\n]+ -> skip ;
//COMMENT : '//' .*? '\r'? '\n' -> skip;
//BLOCK_COMMENT : '/*' .*? '*/' -> skip;

JAVA_BEFORE_SPEC : .*? '/*@' -> skip;
JAVA_AFTER_SPEC : '@*/' .*? -> skip;   	
