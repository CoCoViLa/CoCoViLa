grammar SpecificationLanguage;

import Java8;

options {
  language = Java;
}

metaInterface
    :   'specification' Identifier (superMetaInterface)? '{' specification '}'
    ;
   
superMetaInterface
    :   'super' classOrInterfaceType (',' classOrInterfaceType)*
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
    :   variableModifier? type variableDeclarator (',' variableDeclarator)*
    ;
   
variableModifier
    :   'static'   # staticVariable
    |   'const'    # constantVariable
    ;

variableDeclarator
    :   Identifier ('=' variableInitializer)?                   # variableDeclaratorInitializer
    |   Identifier ('=' variableAssigner)                       # variableDeclaratorAssigner
    |   Identifier ('(' specificationVariableDeclaration ')')?  # specificationVariable
    ;

specificationVariableDeclaration
    :   specificationVariableDeclarator (',' specificationVariableDeclarator)*
    ;

specificationVariableDeclarator
    :   Identifier '=' expression
    ;

variableAssignment
    :   variableIdentifier '=' variableAssigner
    ;
   
axiom
    :   ( inputVariables = variableIdentifierList
        | subtaskList
        | (subtaskList ',' inputVariables = variableIdentifierList)
        )?
        '->'
        outputVariables = variableIdentifierList
        (',' exceptionList)?
        '{' axiomRealization '}'
    ;

axiomRealization
    : Identifier        #method
    | '@table'          #expertTable
    | lambdaExpression  #lambda
    ;

subtask
     //dependent
    :   '[' inputVariables = variableIdentifierList '->' outputVariables = variableIdentifierList ']'
    //independent
    |   '[' (context = classOrInterfaceType '|-')? (inputVariables = variableIdentifierList)? '->' outputVariables = variableIdentifierList ']'
    ;
   
subtaskList
    :   subtask (',' subtask)*
    ;

exceptionList
    :   '(' classOrInterfaceType ')' (',' '(' classOrInterfaceType ')')*
    ;
   
goal
    :   inputVariables = variableIdentifierList? '->' outputVariables = variableIdentifierList
    ;
   
aliasDeclaration
    :   'alias' ('(' type')')? Identifier ('=' aliasStructure)?
    ;
   
aliasStructure
    :   ('('|'[') ( variableAlias=variableIdentifierList? | wildcardAliasName = wildcardAlias )(')'|']')
    ;
   
wildcardAlias
    :   '*.' Identifier
    ;

aliasDefinition
    :   variableIdentifier '=' aliasStructure
    ;

type
    :   classOrInterfaceType ('[' ']')*
    |   primitiveType ('[' ']')*
    |   'void'
    ;

equation
    :   left = expression '=' right = expression
    ;

expression
    :   term
    |   '(' expression ')'
    |   '-' expression
    |   Identifier '(' expression ')'
    |   left = expression op=('*' | '/') right = expression
    |   left = expression op=('+' | '-') right = expression
    |   expression '^' expression
    ;
   
term
    :   IntegerLiteral
    |   FloatingPointLiteral
    |   variableIdentifier
    ;

array
    :   '{' (inArrayVariableAssigner (',' inArrayVariableAssigner)* )? '}'
    ;
    
inArrayVariableAssigner
    :   variableAssigner | variableInitializer
    ;

variableAssigner
    :   array
    /*
      FIXME - string literals as arguments don't work, that's because creator's subtree refs to 'expression' but it is overriden in this grammar,
      if StringLiterals are enabled in 'term', equation solver will fail
    */
    //|   'new' classOrInterfaceType '(' expression (',' expression)* ')'
    |   'new' creator
    | literal
    ;
    
variableInitializer
    :   array
    |   expression
    ;

variableIdentifier
    :   Identifier (('.' Identifier | '*') )* ALIAS_ELEMENT_REF*  ('.' variableIdentifier)?//orig
    |   Identifier '.*' ALIAS_ELEMENT_REF+
    |   Identifier '.*'  ('.' variableIdentifier)+
    ;
   
variableIdentifierList
    :   variableIdentifier (',' variableIdentifier )*
    ;

ALIAS_ELEMENT_REF
    :   '.' DecimalIntegerLiteral+
    ;

JAVA_BEFORE_SPEC : .*? '/*@' -> skip;
JAVA_AFTER_SPEC : '@*/' .*? -> skip;
