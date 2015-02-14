grammar SpecificationLanguage;


options {
  language = Java;
}

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
    : ( annotatedStatement ';' )*
    ;

annotatedStatement
   : annotation* statement
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
	//dependent
	:	'[' inputVariables = variableIdentifierList '->' outputVariables = variableIdentifierList ']'
	//independent
	|	'[' (context = classOrInterfaceType '|-')? (inputVariables = variableIdentifierList)? '->' outputVariables = variableIdentifierList ']'
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
    :	IntegerLiteral
    |	FloatingPointLiteral
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
    |	StringLiteral
    /*
      FIXME - string literals as arguments don't work, that's because creator's subtree refs to 'expression' but it is overriden in this grammar,
      if StringLiterals are enabled in 'term', equation solver will fail
    */
    //|   'new' classOrInterfaceType '(' expression (',' expression)* ')'
    |	'new' creator
    |	'true'
    |	'false'
    ;
    
variableInitializer
    :   array
    |   expression
    ;

variableIdentifier
	:	Identifier (('.' Identifier | '*') )* ALIAS_ELEMENT_REF*  ('.' variableIdentifier)?//orig
	|	Identifier '.*' ALIAS_ELEMENT_REF+
	|	Identifier '.*'  ('.' variableIdentifier)+
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


/* JAVA 8 GRAMMAR */

lambdaExpression
	:	lambdaParameters '->' lambdaBody
	;

lambdaParameters
	:	Identifier
	|	'(' formalParameterList? ')'
	|	'(' inferredFormalParameterList ')'
	;

inferredFormalParameterList
	:	Identifier (',' Identifier)*
	;

lambdaBody
	:	javaexpression
	|	block
	;

/* JAVA 7 GRAMMAR */

// starting point for parsing a java file
compilationUnit
    :   packageDeclaration? importDeclaration* typeDeclaration* EOF
    ;

packageDeclaration
    :   annotation* 'package' qualifiedName ';'
    ;

importDeclaration
    :   'import' 'static'? qualifiedName ('.' '*')? ';'
    ;

typeDeclaration
    :   classOrInterfaceModifier* classDeclaration
    |   classOrInterfaceModifier* enumDeclaration
    |   classOrInterfaceModifier* interfaceDeclaration
    |   classOrInterfaceModifier* annotationTypeDeclaration
    |   ';'
    ;

modifier
    :   classOrInterfaceModifier
    |   (   'native'
        |   'synchronized'
        |   'transient'
        |   'volatile'
        )
    ;

classOrInterfaceModifier
    :   annotation       // class or interface
    |   (   'public'     // class or interface
        |   'protected'  // class or interface
        |   'private'    // class or interface
        |   'static'     // class or interface
        |   'abstract'   // class or interface
        |   'final'      // class only -- does not apply to interfaces
        |   'strictfp'   // class or interface
        )
    ;

javavariableModifier
    :   'final'
    |   annotation
    ;

classDeclaration
    :   'class' Identifier typeParameters?
        ('extends' javatype)?
        ('implements' typeList)?
        classBody
    ;

typeParameters
    :   '<' typeParameter (',' typeParameter)* '>'
    ;

typeParameter
    :   Identifier ('extends' typeBound)?
    ;

typeBound
    :   javatype ('&' javatype)*
    ;

enumDeclaration
    :   ENUM Identifier ('implements' typeList)?
        '{' enumConstants? ','? enumBodyDeclarations? '}'
    ;

enumConstants
    :   enumConstant (',' enumConstant)*
    ;

enumConstant
    :   annotation* Identifier arguments? classBody?
    ;

enumBodyDeclarations
    :   ';' classBodyDeclaration*
    ;

interfaceDeclaration
    :   'interface' Identifier typeParameters? ('extends' typeList)? interfaceBody
    ;

typeList
    :   javatype (',' javatype)*
    ;

classBody
    :   '{' classBodyDeclaration* '}'
    ;

interfaceBody
    :   '{' interfaceBodyDeclaration* '}'
    ;

classBodyDeclaration
    :   ';'
    |   'static'? block
    |   modifier* memberDeclaration
    ;

memberDeclaration
    :   methodDeclaration
    |   genericMethodDeclaration
    |   fieldDeclaration
    |   constructorDeclaration
    |   genericConstructorDeclaration
    |   interfaceDeclaration
    |   annotationTypeDeclaration
    |   classDeclaration
    |   enumDeclaration
    ;

/* We use rule this even for void methods which cannot have [] after parameters.
   This simplifies grammar and we can consider void to be a javatype, which
   renders the [] matching as a context-sensitive issue or a semantic check
   for invalid return javatype after parsing.
 */
methodDeclaration
    :   (javatype|'void') Identifier formalParameters ('[' ']')*
        ('throws' qualifiedNameList)?
        (   methodBody
        |   ';'
        )
    ;

genericMethodDeclaration
    :   typeParameters methodDeclaration
    ;

constructorDeclaration
    :   Identifier formalParameters ('throws' qualifiedNameList)?
        constructorBody
    ;

genericConstructorDeclaration
    :   typeParameters constructorDeclaration
    ;

fieldDeclaration
    :   javatype variableDeclarators ';'
    ;

interfaceBodyDeclaration
    :   modifier* interfaceMemberDeclaration
    |   ';'
    ;

interfaceMemberDeclaration
    :   constDeclaration
    |   interfaceMethodDeclaration
    |   genericInterfaceMethodDeclaration
    |   interfaceDeclaration
    |   annotationTypeDeclaration
    |   classDeclaration
    |   enumDeclaration
    ;

constDeclaration
    :   javatype constantDeclarator (',' constantDeclarator)* ';'
    ;

constantDeclarator
    :   Identifier ('[' ']')* '=' javavariableInitializer
    ;

// see matching of [] comment in methodDeclaratorRest
interfaceMethodDeclaration
    :   (javatype|'void') Identifier formalParameters ('[' ']')*
        ('throws' qualifiedNameList)?
        ';'
    ;

genericInterfaceMethodDeclaration
    :   typeParameters interfaceMethodDeclaration
    ;

variableDeclarators
    :   javavariableDeclarator (',' javavariableDeclarator)*
    ;

javavariableDeclarator
    :   variableDeclaratorId ('=' javavariableInitializer)?
    ;

variableDeclaratorId
    :   Identifier ('[' ']')*
    ;

javavariableInitializer
    :   arrayInitializer
    |   javaexpression
    ;

arrayInitializer
    :   '{' (javavariableInitializer (',' javavariableInitializer)* (',')? )? '}'
    ;

enumConstantName
    :   Identifier
    ;

javatype
    :   classOrInterfaceType ('[' ']')*
    |   primitiveType ('[' ']')*
    ;

classOrInterfaceType
    :   Identifier typeArguments? ('.' Identifier typeArguments? )*
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

typeArguments
    :   '<' typeArgument (',' typeArgument)* '>'
    ;

typeArgument
    :   javatype
    |   '?' (('extends' | 'super') javatype)?
    ;

qualifiedNameList
    :   qualifiedName (',' qualifiedName)*
    ;

formalParameters
    :   '(' formalParameterList? ')'
    ;

formalParameterList
    :   formalParameter (',' formalParameter)* (',' lastFormalParameter)?
    |   lastFormalParameter
    ;

formalParameter
    :   javavariableModifier* javatype variableDeclaratorId
    ;

lastFormalParameter
    :   javavariableModifier* javatype '...' variableDeclaratorId
    ;

methodBody
    :   block
    ;

constructorBody
    :   block
    ;

qualifiedName
    :   Identifier ('.' Identifier)*
    ;

literal
    :   IntegerLiteral
    |   FloatingPointLiteral
    |   CharacterLiteral
    |   StringLiteral
    |   BooleanLiteral
    |   'null'
    ;

// ANNOTATIONS

annotation
    :   '@' annotationName ( '(' ( elementValuePairs | elementValue )? ')' )?
    ;

annotationName : qualifiedName ;

elementValuePairs
    :   elementValuePair (',' elementValuePair)*
    ;

elementValuePair
    :   Identifier '=' elementValue
    ;

elementValue
    :   javaexpression
    |   annotation
    |   elementValueArrayInitializer
    ;

elementValueArrayInitializer
    :   '{' (elementValue (',' elementValue)*)? (',')? '}'
    ;

annotationTypeDeclaration
    :   '@' 'interface' Identifier annotationTypeBody
    ;

annotationTypeBody
    :   '{' (annotationTypeElementDeclaration)* '}'
    ;

annotationTypeElementDeclaration
    :   modifier* annotationTypeElementRest
    |   ';' // this is not allowed by the grammar, but apparently allowed by the actual compiler
    ;

annotationTypeElementRest
    :   javatype annotationMethodOrConstantRest ';'
    |   classDeclaration ';'?
    |   interfaceDeclaration ';'?
    |   enumDeclaration ';'?
    |   annotationTypeDeclaration ';'?
    ;

annotationMethodOrConstantRest
    :   annotationMethodRest
    |   annotationConstantRest
    ;

annotationMethodRest
    :   Identifier '(' ')' defaultValue?
    ;

annotationConstantRest
    :   variableDeclarators
    ;

defaultValue
    :   'default' elementValue
    ;

// STATEMENTS / BLOCKS

block
    :   '{' blockStatement* '}'
    ;

blockStatement
    :   localVariableDeclarationStatement
    |   javastatement
    |   typeDeclaration
    ;

localVariableDeclarationStatement
    :    localVariableDeclaration ';'
    ;

localVariableDeclaration
    :   javavariableModifier* javatype variableDeclarators
    ;

javastatement
    :   block
    |   ASSERT javaexpression (':' javaexpression)? ';'
    |   'if' parExpression javastatement ('else' javastatement)?
    |   'for' '(' forControl ')' javastatement
    |   'while' parExpression javastatement
    |   'do' javastatement 'while' parExpression ';'
    |   'try' block (catchClause+ finallyBlock? | finallyBlock)
    |   'try' resourceSpecification block catchClause* finallyBlock?
    |   'switch' parExpression '{' switchBlockStatementGroup* switchLabel* '}'
    |   'synchronized' parExpression block
    |   'return' javaexpression? ';'
    |   'throw' javaexpression ';'
    |   'break' Identifier? ';'
    |   'continue' Identifier? ';'
    |   ';'
    |   statementExpression ';'
    |   Identifier ':' javastatement
    ;

catchClause
    :   'catch' '(' javavariableModifier* catchType Identifier ')' block
    ;

catchType
    :   qualifiedName ('|' qualifiedName)*
    ;

finallyBlock
    :   'finally' block
    ;

resourceSpecification
    :   '(' resources ';'? ')'
    ;

resources
    :   resource (';' resource)*
    ;

resource
    :   javavariableModifier* classOrInterfaceType variableDeclaratorId '=' javaexpression
    ;

/** Matches cases then statements, both of which are mandatory.
 *  To handle empty cases at the end, we add switchLabel* to javastatement.
 */
switchBlockStatementGroup
    :   switchLabel+ blockStatement+
    ;

switchLabel
    :   'case' constantExpression ':'
    |   'case' enumConstantName ':'
    |   'default' ':'
    ;

forControl
    :   enhancedForControl
    |   forInit? ';' javaexpression? ';' forUpdate?
    ;

forInit
    :   localVariableDeclaration
    |   expressionList
    ;

enhancedForControl
    :   javavariableModifier* javatype variableDeclaratorId ':' javaexpression
    ;

forUpdate
    :   expressionList
    ;

// EXPRESSIONS

parExpression
    :   '(' javaexpression ')'
    ;

expressionList
    :   javaexpression (',' javaexpression)*
    ;

statementExpression
    :   javaexpression
    ;

constantExpression
    :   javaexpression
    ;

javaexpression
    :   primary
    |   javaexpression '.' Identifier
    |   javaexpression '.' 'this'
    |   javaexpression '.' 'new' nonWildcardTypeArguments? innerCreator
    |   javaexpression '.' 'super' superSuffix
    |   javaexpression '.' explicitGenericInvocation
    |   javaexpression '[' javaexpression ']'
    |   javaexpression '(' expressionList? ')'
    |   'new' creator
    |   '(' javatype ')' javaexpression
    |   javaexpression ('++' | '--')
    |   ('+'|'-'|'++'|'--') javaexpression
    |   ('~'|'!') javaexpression
    |   javaexpression ('*'|'/'|'%') javaexpression
    |   javaexpression ('+'|'-') javaexpression
    |   javaexpression ('<' '<' | '>' '>' '>' | '>' '>') javaexpression
    |   javaexpression ('<=' | '>=' | '>' | '<') javaexpression
    |   javaexpression 'instanceof' javatype
    |   javaexpression ('==' | '!=') javaexpression
    |   javaexpression '&' javaexpression
    |   javaexpression '^' javaexpression
    |   javaexpression '|' javaexpression
    |   javaexpression '&&' javaexpression
    |   javaexpression '||' javaexpression
    |   javaexpression '?' javaexpression ':' javaexpression
    |   <assoc=right> javaexpression
        (   '='
        |   '+='
        |   '-='
        |   '*='
        |   '/='
        |   '&='
        |   '|='
        |   '^='
        |   '>>='
        |   '>>>='
        |   '<<='
        |   '%='
        )
        javaexpression
    ;

primary
    :   '(' javaexpression ')'
    |   'this'
    |   'super'
    |   literal
    |   Identifier
    |   javatype '.' 'class'
    |   'void' '.' 'class'
    |   nonWildcardTypeArguments (explicitGenericInvocationSuffix | 'this' arguments)
    ;

creator
    :   nonWildcardTypeArguments createdName classCreatorRest
    |   createdName (arrayCreatorRest | classCreatorRest)
    ;

createdName
    :   Identifier typeArgumentsOrDiamond? ('.' Identifier typeArgumentsOrDiamond?)*
    |   primitiveType
    ;

innerCreator
    :   Identifier nonWildcardTypeArgumentsOrDiamond? classCreatorRest
    ;

arrayCreatorRest
    :   '['
        (   ']' ('[' ']')* arrayInitializer
        |   javaexpression ']' ('[' javaexpression ']')* ('[' ']')*
        )
    ;

classCreatorRest
    :   arguments classBody?
    ;

explicitGenericInvocation
    :   nonWildcardTypeArguments explicitGenericInvocationSuffix
    ;

nonWildcardTypeArguments
    :   '<' typeList '>'
    ;

typeArgumentsOrDiamond
    :   '<' '>'
    |   typeArguments
    ;

nonWildcardTypeArgumentsOrDiamond
    :   '<' '>'
    |   nonWildcardTypeArguments
    ;

superSuffix
    :   arguments
    |   '.' Identifier arguments?
    ;

explicitGenericInvocationSuffix
    :   'super' superSuffix
    |   Identifier arguments
    ;

arguments
    :   '(' expressionList? ')'
    ;

// LEXER

// §3.9 Keywords

ABSTRACT      : 'abstract';
ASSERT        : 'assert';
BOOLEAN       : 'boolean';
BREAK         : 'break';
BYTE          : 'byte';
CASE          : 'case';
CATCH         : 'catch';
CHAR          : 'char';
CLASS         : 'class';
CONST         : 'const';
CONTINUE      : 'continue';
DEFAULT       : 'default';
DO            : 'do';
DOUBLE        : 'double';
ELSE          : 'else';
ENUM          : 'enum';
EXTENDS       : 'extends';
FINAL         : 'final';
FINALLY       : 'finally';
FLOAT         : 'float';
FOR           : 'for';
IF            : 'if';
GOTO          : 'goto';
IMPLEMENTS    : 'implements';
IMPORT        : 'import';
INSTANCEOF    : 'instanceof';
INT           : 'int';
INTERFACE     : 'interface';
LONG          : 'long';
NATIVE        : 'native';
NEW           : 'new';
PACKAGE       : 'package';
PRIVATE       : 'private';
PROTECTED     : 'protected';
PUBLIC        : 'public';
RETURN        : 'return';
SHORT         : 'short';
STATIC        : 'static';
STRICTFP      : 'strictfp';
SUPER         : 'super';
SWITCH        : 'switch';
SYNCHRONIZED  : 'synchronized';
THIS          : 'this';
THROW         : 'throw';
THROWS        : 'throws';
TRANSIENT     : 'transient';
TRY           : 'try';
VOID          : 'void';
VOLATILE      : 'volatile';
WHILE         : 'while';

// §3.10.1 Integer Literals

IntegerLiteral
    :   DecimalIntegerLiteral
    |   HexIntegerLiteral
    |   OctalIntegerLiteral
    |   BinaryIntegerLiteral
    ;

fragment
DecimalIntegerLiteral
    :   DecimalNumeral IntegerTypeSuffix?
    ;

fragment
HexIntegerLiteral
    :   HexNumeral IntegerTypeSuffix?
    ;

fragment
OctalIntegerLiteral
    :   OctalNumeral IntegerTypeSuffix?
    ;

fragment
BinaryIntegerLiteral
    :   BinaryNumeral IntegerTypeSuffix?
    ;

fragment
IntegerTypeSuffix
    :   [lL]
    ;

fragment
DecimalNumeral
    :   '0'
    |   NonZeroDigit (Digits? | Underscores Digits)
    ;

fragment
Digits
    :   Digit (DigitOrUnderscore* Digit)?
    ;

fragment
Digit
    :   '0'
    |   NonZeroDigit
    ;

fragment
NonZeroDigit
    :   [1-9]
    ;

fragment
DigitOrUnderscore
    :   Digit
    |   '_'
    ;

fragment
Underscores
    :   '_'+
    ;

fragment
HexNumeral
    :   '0' [xX] HexDigits
    ;

fragment
HexDigits
    :   HexDigit (HexDigitOrUnderscore* HexDigit)?
    ;

fragment
HexDigit
    :   [0-9a-fA-F]
    ;

fragment
HexDigitOrUnderscore
    :   HexDigit
    |   '_'
    ;

fragment
OctalNumeral
    :   '0' Underscores? OctalDigits
    ;

fragment
OctalDigits
    :   OctalDigit (OctalDigitOrUnderscore* OctalDigit)?
    ;

fragment
OctalDigit
    :   [0-7]
    ;

fragment
OctalDigitOrUnderscore
    :   OctalDigit
    |   '_'
    ;

fragment
BinaryNumeral
    :   '0' [bB] BinaryDigits
    ;

fragment
BinaryDigits
    :   BinaryDigit (BinaryDigitOrUnderscore* BinaryDigit)?
    ;

fragment
BinaryDigit
    :   [01]
    ;

fragment
BinaryDigitOrUnderscore
    :   BinaryDigit
    |   '_'
    ;

// §3.10.2 Floating-Point Literals

FloatingPointLiteral
    :   DecimalFloatingPointLiteral
    |   HexadecimalFloatingPointLiteral
    ;

fragment
DecimalFloatingPointLiteral
    :   Digits '.' Digits? ExponentPart? FloatTypeSuffix?
    |   '.' Digits ExponentPart? FloatTypeSuffix?
    |   Digits ExponentPart FloatTypeSuffix?
    |   Digits FloatTypeSuffix
    ;

fragment
ExponentPart
    :   ExponentIndicator SignedInteger
    ;

fragment
ExponentIndicator
    :   [eE]
    ;

fragment
SignedInteger
    :   Sign? Digits
    ;

fragment
Sign
    :   [+-]
    ;

fragment
FloatTypeSuffix
    :   [fFdD]
    ;

fragment
HexadecimalFloatingPointLiteral
    :   HexSignificand BinaryExponent FloatTypeSuffix?
    ;

fragment
HexSignificand
    :   HexNumeral '.'?
    |   '0' [xX] HexDigits? '.' HexDigits
    ;

fragment
BinaryExponent
    :   BinaryExponentIndicator SignedInteger
    ;

fragment
BinaryExponentIndicator
    :   [pP]
    ;

// §3.10.3 Boolean Literals

BooleanLiteral
    :   'true'
    |   'false'
    ;

// §3.10.4 Character Literals

CharacterLiteral
    :   '\'' SingleCharacter '\''
    |   '\'' EscapeSequence '\''
    ;

fragment
SingleCharacter
    :   ~['\\]
    ;

// §3.10.5 String Literals

StringLiteral
    :   '"' StringCharacters? '"'
    ;

fragment
StringCharacters
    :   StringCharacter+
    ;

fragment
StringCharacter
    :   ~["\\]
    |   EscapeSequence
    ;

// §3.10.6 Escape Sequences for Character and String Literals

fragment
EscapeSequence
    :   '\\' [btnfr"'\\]
    |   OctalEscape
    |   UnicodeEscape
    ;

fragment
OctalEscape
    :   '\\' OctalDigit
    |   '\\' OctalDigit OctalDigit
    |   '\\' ZeroToThree OctalDigit OctalDigit
    ;

fragment
UnicodeEscape
    :   '\\' 'u' HexDigit HexDigit HexDigit HexDigit
    ;

fragment
ZeroToThree
    :   [0-3]
    ;

// §3.10.7 The Null Literal

NullLiteral
    :   'null'
    ;

// §3.11 Separators

LPAREN          : '(';
RPAREN          : ')';
LBRACE          : '{';
RBRACE          : '}';
LBRACK          : '[';
RBRACK          : ']';
SEMI            : ';';
COMMA           : ',';
DOT             : '.';

// §3.12 Operators

ASSIGN          : '=';
GT              : '>';
LT              : '<';
BANG            : '!';
TILDE           : '~';
QUESTION        : '?';
COLON           : ':';
EQUAL           : '==';
LE              : '<=';
GE              : '>=';
NOTEQUAL        : '!=';
AND             : '&&';
OR              : '||';
INC             : '++';
DEC             : '--';
ADD             : '+';
SUB             : '-';
MUL             : '*';
DIV             : '/';
BITAND          : '&';
BITOR           : '|';
CARET           : '^';
MOD             : '%';

ADD_ASSIGN      : '+=';
SUB_ASSIGN      : '-=';
MUL_ASSIGN      : '*=';
DIV_ASSIGN      : '/=';
AND_ASSIGN      : '&=';
OR_ASSIGN       : '|=';
XOR_ASSIGN      : '^=';
MOD_ASSIGN      : '%=';
LSHIFT_ASSIGN   : '<<=';
RSHIFT_ASSIGN   : '>>=';
URSHIFT_ASSIGN  : '>>>=';

// §3.8 Identifiers (must appear after all keywords in the grammar)

Identifier
    :   JavaLetter JavaLetterOrDigit*
    ;

fragment
JavaLetter
    :   [a-zA-Z$_] // these are the "java letters" below 0xFF
    |   // covers all characters above 0xFF which are not a surrogate
        ~[\u0000-\u00FF\uD800-\uDBFF]
        {Character.isJavaIdentifierStart(_input.LA(-1))}?
    |   // covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
        [\uD800-\uDBFF] [\uDC00-\uDFFF]
        {Character.isJavaIdentifierStart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)))}?
    ;

fragment
JavaLetterOrDigit
    :   [a-zA-Z0-9$_] // these are the "java letters or digits" below 0xFF
    |   // covers all characters above 0xFF which are not a surrogate
        ~[\u0000-\u00FF\uD800-\uDBFF]
        {Character.isJavaIdentifierPart(_input.LA(-1))}?
    |   // covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
        [\uD800-\uDBFF] [\uDC00-\uDFFF]
        {Character.isJavaIdentifierPart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)))}?
    ;

//
// Additional symbols not defined in the lexical specification
//

AT : '@';
ELLIPSIS : '...';

//
// Whitespace and comments
//

WS  :  [ \t\r\n\u000C]+ -> skip
    ;

COMMENT
    :   '/*' .*? '*/' -> skip
    ;

LINE_COMMENT
    :   '//' ~[\r\n]* -> skip
    ;
