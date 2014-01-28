/*
 * Sonar PHP Plugin
 * Copyright (C) 2010 Codehaus Sonar Plugins
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.php.parser;

import org.sonar.php.lexer.PHPKeyword;
import org.sonar.php.lexer.PHPPunctuator;
import org.sonar.sslr.grammar.GrammarRuleKey;
import org.sonar.sslr.grammar.LexerfulGrammarBuilder;

import static com.sonar.sslr.api.GenericTokenType.EOF;
import static com.sonar.sslr.api.GenericTokenType.IDENTIFIER;
import static com.sonar.sslr.api.GenericTokenType.LITERAL;
import static org.sonar.php.lexer.PHPKeyword.ABSTRACT;
import static org.sonar.php.lexer.PHPKeyword.ARRAY;
import static org.sonar.php.lexer.PHPKeyword.AS;
import static org.sonar.php.lexer.PHPKeyword.BREAK;
import static org.sonar.php.lexer.PHPKeyword.CALLABLE;
import static org.sonar.php.lexer.PHPKeyword.CASE;
import static org.sonar.php.lexer.PHPKeyword.CATCH;
import static org.sonar.php.lexer.PHPKeyword.CLASS;
import static org.sonar.php.lexer.PHPKeyword.CLONE;
import static org.sonar.php.lexer.PHPKeyword.CONST;
import static org.sonar.php.lexer.PHPKeyword.CONTINUE;
import static org.sonar.php.lexer.PHPKeyword.DECLARE;
import static org.sonar.php.lexer.PHPKeyword.DEFAULT;
import static org.sonar.php.lexer.PHPKeyword.DIE;
import static org.sonar.php.lexer.PHPKeyword.DO;
import static org.sonar.php.lexer.PHPKeyword.ECHO;
import static org.sonar.php.lexer.PHPKeyword.ELSE;
import static org.sonar.php.lexer.PHPKeyword.ELSEIF;
import static org.sonar.php.lexer.PHPKeyword.EMPTY;
import static org.sonar.php.lexer.PHPKeyword.ENDDECLARE;
import static org.sonar.php.lexer.PHPKeyword.ENDFOR;
import static org.sonar.php.lexer.PHPKeyword.ENDFOREACH;
import static org.sonar.php.lexer.PHPKeyword.ENDIF;
import static org.sonar.php.lexer.PHPKeyword.ENDSWITCH;
import static org.sonar.php.lexer.PHPKeyword.ENDWHILE;
import static org.sonar.php.lexer.PHPKeyword.EVAL;
import static org.sonar.php.lexer.PHPKeyword.EXIT;
import static org.sonar.php.lexer.PHPKeyword.EXTENDS;
import static org.sonar.php.lexer.PHPKeyword.FINAL;
import static org.sonar.php.lexer.PHPKeyword.FINALLY;
import static org.sonar.php.lexer.PHPKeyword.FOR;
import static org.sonar.php.lexer.PHPKeyword.FOREACH;
import static org.sonar.php.lexer.PHPKeyword.FUNCTION;
import static org.sonar.php.lexer.PHPKeyword.GLOBAL;
import static org.sonar.php.lexer.PHPKeyword.GOTO;
import static org.sonar.php.lexer.PHPKeyword.HALT_COMPILER;
import static org.sonar.php.lexer.PHPKeyword.IF;
import static org.sonar.php.lexer.PHPKeyword.IMPLEMENTS;
import static org.sonar.php.lexer.PHPKeyword.INCLUDE;
import static org.sonar.php.lexer.PHPKeyword.INCLUDE_ONCE;
import static org.sonar.php.lexer.PHPKeyword.INSTANCEOF;
import static org.sonar.php.lexer.PHPKeyword.INSTEADOF;
import static org.sonar.php.lexer.PHPKeyword.INTERFACE;
import static org.sonar.php.lexer.PHPKeyword.ISSET;
import static org.sonar.php.lexer.PHPKeyword.LIST;
import static org.sonar.php.lexer.PHPKeyword.NAMESPACE;
import static org.sonar.php.lexer.PHPKeyword.NEW;
import static org.sonar.php.lexer.PHPKeyword.PRINT;
import static org.sonar.php.lexer.PHPKeyword.PRIVATE;
import static org.sonar.php.lexer.PHPKeyword.PROTECTED;
import static org.sonar.php.lexer.PHPKeyword.PUBLIC;
import static org.sonar.php.lexer.PHPKeyword.REQUIRE;
import static org.sonar.php.lexer.PHPKeyword.REQUIRE_ONCE;
import static org.sonar.php.lexer.PHPKeyword.RETURN;
import static org.sonar.php.lexer.PHPKeyword.STATIC;
import static org.sonar.php.lexer.PHPKeyword.SWITCH;
import static org.sonar.php.lexer.PHPKeyword.THROW;
import static org.sonar.php.lexer.PHPKeyword.TRAIT;
import static org.sonar.php.lexer.PHPKeyword.TRY;
import static org.sonar.php.lexer.PHPKeyword.UNSET;
import static org.sonar.php.lexer.PHPKeyword.USE;
import static org.sonar.php.lexer.PHPKeyword.VAR;
import static org.sonar.php.lexer.PHPKeyword.WHILE;
import static org.sonar.php.lexer.PHPKeyword.YIELD;
import static org.sonar.php.lexer.PHPPunctuator.AND;
import static org.sonar.php.lexer.PHPPunctuator.ANDAND;
import static org.sonar.php.lexer.PHPPunctuator.ANDEQUAL;
import static org.sonar.php.lexer.PHPPunctuator.ARROW;
import static org.sonar.php.lexer.PHPPunctuator.BANG;
import static org.sonar.php.lexer.PHPPunctuator.COLON;
import static org.sonar.php.lexer.PHPPunctuator.COMMA;
import static org.sonar.php.lexer.PHPPunctuator.CONCATEQUAL;
import static org.sonar.php.lexer.PHPPunctuator.DEC;
import static org.sonar.php.lexer.PHPPunctuator.DIV;
import static org.sonar.php.lexer.PHPPunctuator.DIVEQUAL;
import static org.sonar.php.lexer.PHPPunctuator.DOLAR;
import static org.sonar.php.lexer.PHPPunctuator.DOLAR_LCURLY;
import static org.sonar.php.lexer.PHPPunctuator.DOT;
import static org.sonar.php.lexer.PHPPunctuator.DOUBLEARROW;
import static org.sonar.php.lexer.PHPPunctuator.DOUBLECOLON;
import static org.sonar.php.lexer.PHPPunctuator.ELIPSIS;
import static org.sonar.php.lexer.PHPPunctuator.EQU;
import static org.sonar.php.lexer.PHPPunctuator.EQUAL;
import static org.sonar.php.lexer.PHPPunctuator.EQUAL2;
import static org.sonar.php.lexer.PHPPunctuator.GE;
import static org.sonar.php.lexer.PHPPunctuator.GT;
import static org.sonar.php.lexer.PHPPunctuator.INC;
import static org.sonar.php.lexer.PHPPunctuator.LBRACKET;
import static org.sonar.php.lexer.PHPPunctuator.LCURLYBRACE;
import static org.sonar.php.lexer.PHPPunctuator.LE;
import static org.sonar.php.lexer.PHPPunctuator.LPARENTHESIS;
import static org.sonar.php.lexer.PHPPunctuator.LT;
import static org.sonar.php.lexer.PHPPunctuator.MINUS;
import static org.sonar.php.lexer.PHPPunctuator.MINUS_EQU;
import static org.sonar.php.lexer.PHPPunctuator.MOD;
import static org.sonar.php.lexer.PHPPunctuator.MOD_EQU;
import static org.sonar.php.lexer.PHPPunctuator.NOTEQUAL;
import static org.sonar.php.lexer.PHPPunctuator.NOTEQUAL2;
import static org.sonar.php.lexer.PHPPunctuator.NOTEQUALBIS;
import static org.sonar.php.lexer.PHPPunctuator.NS_SEPARATOR;
import static org.sonar.php.lexer.PHPPunctuator.OR;
import static org.sonar.php.lexer.PHPPunctuator.OROR;
import static org.sonar.php.lexer.PHPPunctuator.OR_EQU;
import static org.sonar.php.lexer.PHPPunctuator.PLUS;
import static org.sonar.php.lexer.PHPPunctuator.PLUS_EQU;
import static org.sonar.php.lexer.PHPPunctuator.QUERY;
import static org.sonar.php.lexer.PHPPunctuator.RBRACKET;
import static org.sonar.php.lexer.PHPPunctuator.RCURLYBRACE;
import static org.sonar.php.lexer.PHPPunctuator.RPARENTHESIS;
import static org.sonar.php.lexer.PHPPunctuator.SEMICOLON;
import static org.sonar.php.lexer.PHPPunctuator.SL;
import static org.sonar.php.lexer.PHPPunctuator.SL_EQU;
import static org.sonar.php.lexer.PHPPunctuator.SR;
import static org.sonar.php.lexer.PHPPunctuator.SR_EQU;
import static org.sonar.php.lexer.PHPPunctuator.STAR;
import static org.sonar.php.lexer.PHPPunctuator.STAR_EQU;
import static org.sonar.php.lexer.PHPPunctuator.TILDA;
import static org.sonar.php.lexer.PHPPunctuator.XOR;
import static org.sonar.php.lexer.PHPPunctuator.XOR_EQU;
import static org.sonar.php.lexer.PHPTokenType.CLOSE_TAG;
import static org.sonar.php.lexer.PHPTokenType.OPEN_TAG;
import static org.sonar.php.lexer.PHPTokenType.VAR_IDENTIFIER;

public enum PHPGrammar implements GrammarRuleKey {

  COMPILATION_UNIT,
  TOP_STATEMENT_LIST,
  TOP_STATEMENT,
  NAMESPACE_STATEMENT,
  USE_STATEMENT,
  USE_DECLARATIONS,
  USE_DECLARATION,
  USE_FUNCTION_DECLARATION_STATEMENT,
  USE_CONST_DECLARATION_STATEMENT,
  USE_FUNCTION_DECLARATIONS,
  USE_FUNCTION_DECLARATION,
  HALT_COMPILER_STATMENT,

  NAMESPACE_NAME,
  UNQUALIFIED_NAME,
  QUALIFIED_NAME,
  FULLY_QUALIFIED_NAME,

  REFERENCE,
  FUNCTION_DECLARATION,
  CLASS_DECLARATION,
  CLASS_ENTRY_TYPE,
  CLASS_TYPE,
  FULLY_QUALIFIED_CLASS_NAME,
  INTERFACE_LIST,
  INTERFACE_DECLARATION,
  INTERFACE_EXTENDS_LIST,
  EXTENDS_FROM,
  IMPLEMENTS_LIST,
  CONSTANT_DECLARATION,
  CONSTANT_VAR_LIST,
  CONSTANT_VAR,


  STATEMENT,
  EMPTY_STATEMENT,
  LABEL,
  BLOCK,
  INNER_STATEMENT_LIST,
  CLASS_STATEMENT,
  IF_STATEMENT,
  ELSIF_LIST,
  ELSE_CLAUSE,
  ALTERNATIVE_IF_STATEMENT,
  ALTERNATIVE_ELSIF_LIST,
  ALTERNATIVE_ELSE_CLAUSE,
  WHILE_STATEMENT,
  INNER_WHILE_STATEMENT,
  DO_WHILE_STATEMENT,
  FOR_STATEMENT,
  FOR_EXRR,
  INNER_FOR_STATEMENT,
  FOREACH_STATEMENT,
  FOREACH_EXPR,
  FOREACH_VARIABLE,
  INNER_FOREACH_STATEMENT,
  SWITCH_STATEMENT,
  SWITCH_CASE_LIST,
  CASE_LIST,
  CASE_CLAUSE,
  DEFAULT_CLAUSE,
  CASE_SEPARTOR,
  BREAK_STATEMENT,
  CONTINUE_STATEMENT,
  RETURN_STATEMENT,
  DECLARE_STATEMENT,
  DECLARE_LIST,
  INNER_DECLARE_STATEMENT,
  TRY_STATEMENT,
  CATCH_STATEMENT,
  FINALLY_STATEMENT,
  THROW_STATEMENT,
  GOTO_STATEMENT,
  YIELD_STATEMENT,
  GLOBAL_STATEMENT,
  GLOBAL_VAR_LIST,
  GLOBAL_VAR,
  STATIC_STATEMENT,
  STATIC_VAR_LIST,
  STATIC_VAR,
  ECHO_STATEMENT,
  UNSET_VARIABLE_STATEMENT,
  UNSET_VARIABLES,
  PARAMETER_LIST,
  PARAMETER,
  OPTIONAL_CLASS_TYPE,
  METHOD_DECLARATION,
  METHOD_BODY,
  VARIABLE_MODIFIERS,
  CLASS_VARIABLE_DECLARATION,
  VARIABLE_DECLARATION,
  MEMBER_MODIFIER,
  CLASS_CONSTANT_DECLARATION,
  MEMBER_CONST_DECLARATION,
  TRAIT_USE_STATEMENT,
  TRAIT_METHOD_REFERENCE_FULLY_QUALIFIED,
  TRAIT_METHOD_REFERENCE,
  TRAIT_ALIAS,
  TRAIT_REFERENCE_LIST,
  TRAIT_PRECEDENCE,
  TRAIT_ADAPTATION_STATEMENT,
  TRAIT_ADAPTATIONS,


  VARIABLE,
  ALIAS_VARIABLE,
  VARIABLE_NAME,
  VARIABLE_WITHOUT_OBJECTS,
  EXPRESSION_STATEMENT,
  REFERENCE_VARIABLE,
  SIMPLE_INDIRECT_REFERENCE,
  BASE_VARIABLE,
  STATIC_MEMBER,
  COMPOUND_VARIABLE,
  CLASS_NAME,
  FUNCTION_CALL,
  FUNCTION_CALL_PARAMETER_LIST,
  PARAMETER_LIST_FOR_CALL,
  BASE_VARIABLE_WITH_FUNCTION_CALLS,
  DIMENSIONAL_OFFSET,
  STATIC_SCALAR,
  OBJECT_DIM_LIST,
  OBJECT_PROPERTY,
  VARIABLE_PROPERTY,
  ARRAY_METHOD_DEREFERENCE,

  PARENTHESIS_EXPRESSION,

  YIELD_EXPRESSION,
  COMBINED_SCALAR,
  STATIC_SCALAR_VALUE,
  COMMON_SCALAR,
  STATIC_CLASS_NAME_SCALAR,
  STATIC_CLASS_CONSTANT_SCALAR,
  STATIC_ARRAY_PAIR_LIST,
  STATIC_ARRAY_PAIR,
  STATIC_UNARY_EXPR,
  STATIC_MULTIPLICATIVE_EXPR,
  MULIPLICATIVE_OPERATOR,
  STATIC_ADDITIVE_EXPR,
  STATIC_SHIFT_EXPR,
  SHIFT_OPERATOR,
  STATIC_RELATIONAL_EXPR,
  RELATIONAL_OPERATOR,
  STATIC_EQUALITY_EXPR,
  EQUALITY_OPERATOR,
  STATIC_CONCATENATION_EXPR,
  STATIC_BITEWISE_AND_EXPR,
  STATIC_BITEWISE_XOR_EXPR,
  STATIC_BITEWISE_OR_EXPR,
  STATIC_LOGICAL_AND_EXPR,
  LOGICAL_AND_OPERATOR,
  STATIC_LOGICAL_XOR_EXPR,
  STATIC_LOGICAL_OR_EXPR,
  LOGICAL_OR_OPERATOR,
  STATIC_CONDITIONAL_EXPR,
  STATIC_OPERATION_EXPR,
  LEXICAL_VARS,
  LEXICAL_VAR_LIST,
  LEXICAL_VAR,
  LOGICAL_XOR_EXPR,
  LOGICAL_OR_EXPR,
  BITEWISE_AND_EXPR,
  BITEWISE_XOR_EXPR,
  BITEWISE_OR_EXPR,
  LOGICAL_AND_EXPR,
  CONDITIONAL_EXPR,
  ASSIGNMENT_EXPR,
  MULTIPLICATIVE_EXPR,
  ADDITIVE_EXPR,
  ADDITIVE_OPERATOR,
  SHIFT_EXPR,
  RELATIONAL_EXPR,
  EQUALITY_EXPR,
  CONCATENATION_EXPR,
  POSTFIX_EXPR,
  UNARY_EXPR,
  ASSIGNMENT_OPERATOR,
  COMPOUND_ASSIGNMENT,
  CAST_TYPE,
  LOGICAL_ASSIGNMENT,
  INTERNAL_FUNCTION,
  CLASS_NAME_REFERENCE,
  NEW_EXPR,
  COMBINED_SCALAR_OFFSET,
  STATIC_COMBINED_SCALAR,
  ARRAY_PAIR_LIST,
  ARRAY_PAIR,
  CLASS_CONSTANT,
  EXIT_EXPR,
  LIST_EXPR,
  CAST_EXPR,
  LIST_ASSIGNMENT_EXPR,
  ASSIGNMENT_LIST_ELEMENT,
  ASSIGNMENT_LIST,
  SCALAR;

  public static LexerfulGrammarBuilder create() {
    LexerfulGrammarBuilder b = LexerfulGrammarBuilder.create();

    b.rule(COMPILATION_UNIT).is(
      b.sequence(OPEN_TAG, TOP_STATEMENT_LIST, b.optional(CLOSE_TAG)),
      EOF);

    declaration(b);
    statement(b);
    expression(b);

    b.setRootRule(COMPILATION_UNIT);

    return b;
  }

  public static void expression(LexerfulGrammarBuilder b) {
    b.rule(PARENTHESIS_EXPRESSION).is(LPARENTHESIS, b.firstOf(YIELD_EXPRESSION, ASSIGNMENT_EXPR), RPARENTHESIS);

    b.rule(YIELD_EXPRESSION).is(YIELD, ASSIGNMENT_EXPR, b.optional(DOUBLEARROW, ASSIGNMENT_EXPR));

    b.rule(BASE_VARIABLE).is(b.firstOf(
      VARIABLE_WITHOUT_OBJECTS,
      STATIC_MEMBER));

    b.rule(BASE_VARIABLE_WITH_FUNCTION_CALLS).is(b.firstOf(
      b.sequence(FUNCTION_CALL, b.zeroOrMore(DIMENSIONAL_OFFSET)),
      BASE_VARIABLE));

    b.rule(SIMPLE_INDIRECT_REFERENCE).is(b.oneOrMore(DOLAR));

    b.rule(REFERENCE_VARIABLE).is(COMPOUND_VARIABLE, b.zeroOrMore(b.firstOf(
      DIMENSIONAL_OFFSET,
      b.sequence(LCURLYBRACE, ASSIGNMENT_EXPR, RCURLYBRACE))));

    b.rule(VARIABLE_WITHOUT_OBJECTS).is(b.optional(SIMPLE_INDIRECT_REFERENCE), REFERENCE_VARIABLE);

    b.rule(COMPOUND_VARIABLE).is(b.firstOf(
      VAR_IDENTIFIER,
      b.sequence(DOLAR_LCURLY, ASSIGNMENT_EXPR, RCURLYBRACE)));

    b.rule(CLASS_NAME).is(b.firstOf(STATIC, FULLY_QUALIFIED_CLASS_NAME));

    b.rule(STATIC_MEMBER).is(b.firstOf(
      CLASS_NAME,
      REFERENCE_VARIABLE
    ), DOUBLECOLON, VARIABLE_WITHOUT_OBJECTS);

    // Function call
    b.rule(FUNCTION_CALL).is(b.firstOf(
      b.sequence(NAMESPACE, FULLY_QUALIFIED_NAME),
      b.sequence(CLASS_NAME, DOUBLECOLON, b.firstOf(VARIABLE_NAME, VARIABLE_WITHOUT_OBJECTS)),
      b.sequence(REFERENCE_VARIABLE, DOUBLECOLON, b.firstOf(VARIABLE_NAME, VARIABLE_WITHOUT_OBJECTS)),
      NAMESPACE_NAME,
      VARIABLE_WITHOUT_OBJECTS
    ), FUNCTION_CALL_PARAMETER_LIST);

    b.rule(FUNCTION_CALL_PARAMETER_LIST).is(LPARENTHESIS, b.optional(PARAMETER_LIST_FOR_CALL, b.zeroOrMore(COMMA, PARAMETER_LIST_FOR_CALL)), RPARENTHESIS);
    b.rule(PARAMETER_LIST_FOR_CALL).is(b.firstOf(ALIAS_VARIABLE, b.sequence(b.optional(ELIPSIS), ASSIGNMENT_EXPR), YIELD_EXPRESSION));

    b.rule(VARIABLE_NAME).is(b.firstOf(
      IDENTIFIER,
      b.sequence(LCURLYBRACE, ASSIGNMENT_EXPR, RCURLYBRACE)));

    b.rule(DIMENSIONAL_OFFSET).is(LBRACKET, b.optional(ASSIGNMENT_EXPR), RBRACKET);
    b.rule(ALIAS_VARIABLE).is(AND, VARIABLE);

    b.rule(VARIABLE).is(BASE_VARIABLE_WITH_FUNCTION_CALLS, b.zeroOrMore(VARIABLE_PROPERTY));
    b.rule(VARIABLE_PROPERTY).is(ARROW, OBJECT_PROPERTY, b.optional(ARRAY_METHOD_DEREFERENCE));
    b.rule(OBJECT_PROPERTY).is(b.firstOf(OBJECT_DIM_LIST, VARIABLE_WITHOUT_OBJECTS));
    b.rule(OBJECT_DIM_LIST).is(VARIABLE_NAME, b.zeroOrMore(b.firstOf(
      b.sequence(LCURLYBRACE, ASSIGNMENT_EXPR, RCURLYBRACE),
      DIMENSIONAL_OFFSET
    )));
    /*can be empty if method_or_not */
    b.rule(ARRAY_METHOD_DEREFERENCE).is(FUNCTION_CALL_PARAMETER_LIST, b.zeroOrMore(DIMENSIONAL_OFFSET));

    b.rule(STATIC_SCALAR).is(b.firstOf(STATIC_COMBINED_SCALAR, STATIC_OPERATION_EXPR));

    b.rule(STATIC_COMBINED_SCALAR).is(b.firstOf(
      b.sequence(ARRAY, LPARENTHESIS, b.optional(ARRAY_PAIR_LIST), RPARENTHESIS),
      b.sequence(LBRACKET, b.optional(ARRAY_PAIR_LIST), RBRACKET)));

    b.rule(COMBINED_SCALAR_OFFSET).is(COMBINED_SCALAR /*TODO: or T_CONSTANT_ENCAPSED_STRING -> check syntax*/, b.zeroOrMore(DIMENSIONAL_OFFSET)); // TODO: TEST

    b.rule(COMBINED_SCALAR).is(b.firstOf( //TODO: TEST
      b.sequence(ARRAY, LPARENTHESIS, b.optional(ARRAY_PAIR_LIST), RPARENTHESIS),
      b.sequence(LBRACKET, b.optional(ARRAY_PAIR_LIST), RBRACKET)));

    b.rule(ARRAY_PAIR_LIST).is(ARRAY_PAIR, b.zeroOrMore(COMMA, ARRAY_PAIR), b.optional(COMMA));
    b.rule(ARRAY_PAIR).is(b.firstOf( // TODO: TEST
      b.sequence(ASSIGNMENT_EXPR, b.optional(DOUBLEARROW, b.firstOf(ALIAS_VARIABLE, ASSIGNMENT_EXPR))),
      ALIAS_VARIABLE));
    b.rule(STATIC_ARRAY_PAIR_LIST).is(STATIC_ARRAY_PAIR, b.zeroOrMore(COMMA, STATIC_ARRAY_PAIR), b.optional(COMMA));
    b.rule(STATIC_ARRAY_PAIR).is(STATIC_SCALAR, b.optional(DOUBLEARROW, STATIC_SCALAR));

    b.rule(STATIC_SCALAR_VALUE).is(b.firstOf(
      COMMON_SCALAR,
      STATIC_CLASS_NAME_SCALAR,
      STATIC_CLASS_CONSTANT_SCALAR,
      FULLY_QUALIFIED_CLASS_NAME));

    b.rule(STATIC_CLASS_NAME_SCALAR).is(CLASS_NAME, DOUBLECOLON, CLASS);
    b.rule(STATIC_CLASS_CONSTANT_SCALAR).is(CLASS_NAME, DOUBLECOLON, IDENTIFIER);
    b.rule(COMMON_SCALAR).is(b.firstOf( // TODO: HEREDOC & are exponents exclude ?
      LITERAL,
      "__CLASS__",
      "__FILE__",
      "__DIR__",
      "__FUNCTION__",
      "__LINE__",
      "__METHOD__",
      "__NAMESPACE__",
      "__TRAIT__"));

    // Unary expression
    b.rule(STATIC_UNARY_EXPR).is(b.firstOf(
      b.sequence(b.firstOf(PLUS, MINUS, TILDA, BANG), STATIC_SCALAR_VALUE),
      STATIC_SCALAR_VALUE));

    // Binary expressions
    b.rule(STATIC_CONCATENATION_EXPR).is(STATIC_UNARY_EXPR, b.zeroOrMore(DOT, STATIC_UNARY_EXPR));

    b.rule(STATIC_MULTIPLICATIVE_EXPR).is(STATIC_CONCATENATION_EXPR, b.zeroOrMore(MULIPLICATIVE_OPERATOR, STATIC_CONCATENATION_EXPR)); // TODO: CHeck changement unary -> concatenation
    b.rule(MULIPLICATIVE_OPERATOR).is(b.firstOf(STAR, DIV, MOD));

    b.rule(STATIC_ADDITIVE_EXPR).is(STATIC_MULTIPLICATIVE_EXPR, b.zeroOrMore(ADDITIVE_OPERATOR, STATIC_MULTIPLICATIVE_EXPR));
    b.rule(ADDITIVE_OPERATOR).is(b.firstOf(PLUS, MINUS));

    b.rule(STATIC_SHIFT_EXPR).is(STATIC_ADDITIVE_EXPR, b.zeroOrMore(SHIFT_OPERATOR, STATIC_ADDITIVE_EXPR));
    b.rule(SHIFT_OPERATOR).is(b.firstOf(SL, SR));

    b.rule(STATIC_RELATIONAL_EXPR).is(STATIC_SHIFT_EXPR, b.zeroOrMore(RELATIONAL_OPERATOR, STATIC_SHIFT_EXPR));
    b.rule(RELATIONAL_OPERATOR).is(b.firstOf(LE, GE, LT, GT));

    b.rule(STATIC_EQUALITY_EXPR).is(STATIC_RELATIONAL_EXPR, b.zeroOrMore(EQUALITY_OPERATOR, STATIC_RELATIONAL_EXPR));
    b.rule(EQUALITY_OPERATOR).is(b.firstOf(
      NOTEQUAL,
      NOTEQUAL2,
      EQUAL,
      EQUAL2,
      NOTEQUALBIS));

    b.rule(STATIC_BITEWISE_AND_EXPR).is(STATIC_EQUALITY_EXPR, b.zeroOrMore(AND, STATIC_EQUALITY_EXPR));
    b.rule(STATIC_BITEWISE_XOR_EXPR).is(STATIC_BITEWISE_AND_EXPR, b.zeroOrMore(XOR, STATIC_BITEWISE_AND_EXPR));
    b.rule(STATIC_BITEWISE_OR_EXPR).is(STATIC_BITEWISE_XOR_EXPR, b.zeroOrMore(OR, STATIC_BITEWISE_XOR_EXPR));
    b.rule(STATIC_LOGICAL_AND_EXPR).is(STATIC_BITEWISE_OR_EXPR, b.zeroOrMore(LOGICAL_AND_OPERATOR, STATIC_BITEWISE_XOR_EXPR));
    b.rule(LOGICAL_AND_OPERATOR).is(b.firstOf(ANDAND, PHPKeyword.AND));

    b.rule(STATIC_LOGICAL_XOR_EXPR).is(STATIC_LOGICAL_AND_EXPR, b.zeroOrMore(PHPKeyword.XOR, STATIC_LOGICAL_AND_EXPR));
    b.rule(STATIC_LOGICAL_OR_EXPR).is(STATIC_LOGICAL_XOR_EXPR, b.zeroOrMore(LOGICAL_OR_OPERATOR, STATIC_LOGICAL_XOR_EXPR));
    b.rule(LOGICAL_OR_OPERATOR).is(b.firstOf(OROR, PHPKeyword.OR));

    // Conditional expression
    b.rule(STATIC_CONDITIONAL_EXPR).is(STATIC_LOGICAL_OR_EXPR, b.optional(QUERY, b.optional(STATIC_SCALAR_VALUE), COLON, STATIC_SCALAR_VALUE));
    b.rule(STATIC_OPERATION_EXPR).is(b.firstOf(
      b.sequence(LPARENTHESIS, STATIC_CONDITIONAL_EXPR, RPARENTHESIS),
      STATIC_LOGICAL_OR_EXPR));

    b.rule(CAST_EXPR).is(LPARENTHESIS, CAST_TYPE, RPARENTHESIS, ASSIGNMENT_EXPR);
    b.rule(CAST_TYPE).is(LPARENTHESIS, b.firstOf("INTEGER", "INT", "DOUBLE", "FLOAT", "STRING", ARRAY, "OBJECT", "BOOLEAN", "BOOL", UNSET), RPARENTHESIS);


    b.rule(POSTFIX_EXPR).is(b.firstOf( // TODO: to complete
      //YIELD, TODO: check
      COMBINED_SCALAR_OFFSET,
      VARIABLE,
      SCALAR,
      NEW_EXPR,
      EXIT_EXPR,
      LIST_ASSIGNMENT_EXPR,
      PARENTHESIS_EXPRESSION,
      INTERNAL_FUNCTION),
      b.optional(b.firstOf(
        INC,
        DEC,
        b.sequence(INSTANCEOF, CLASS_NAME_REFERENCE))));

    b.rule(SCALAR).is(b.firstOf( // TODO: to complete
      CLASS_CONSTANT,
      STATIC_CLASS_NAME_SCALAR,
      FULLY_QUALIFIED_CLASS_NAME,
      VAR_IDENTIFIER,
      COMMON_SCALAR));

    b.rule(CLASS_CONSTANT).is(b.firstOf(CLASS_NAME, REFERENCE_VARIABLE), DOUBLECOLON, IDENTIFIER);

    b.rule(EXIT_EXPR).is(b.firstOf(EXIT, DIE), b.optional(LPARENTHESIS, b.optional(ASSIGNMENT_EXPR), RPARENTHESIS));

    b.rule(LIST_ASSIGNMENT_EXPR).is(LIST_EXPR, EQU, ASSIGNMENT_EXPR);
    b.rule(LIST_EXPR).is(LIST, b.optional(LPARENTHESIS, ASSIGNMENT_LIST, RPARENTHESIS));
    b.rule(ASSIGNMENT_LIST).is(b.optional(ASSIGNMENT_LIST_ELEMENT), b.zeroOrMore(COMMA, b.optional(ASSIGNMENT_LIST_ELEMENT)));
    b.rule(ASSIGNMENT_LIST_ELEMENT).is(b.firstOf(VARIABLE, LIST_ASSIGNMENT_EXPR));


    b.rule(INTERNAL_FUNCTION).is(b.firstOf(
      b.sequence(ISSET, LPARENTHESIS, ASSIGNMENT_EXPR, b.zeroOrMore(COMMA, ASSIGNMENT_EXPR), RPARENTHESIS),
      b.sequence(EMPTY, LPARENTHESIS, ASSIGNMENT_EXPR, RPARENTHESIS),
      b.sequence(INCLUDE, ASSIGNMENT_EXPR),
      b.sequence(INCLUDE_ONCE, ASSIGNMENT_EXPR),
      b.sequence(EVAL, LPARENTHESIS, ASSIGNMENT_EXPR, RPARENTHESIS),
      b.sequence(REQUIRE, ASSIGNMENT_EXPR),
      b.sequence(REQUIRE_ONCE, ASSIGNMENT_EXPR),
      b.sequence(CLONE, ASSIGNMENT_EXPR),
      b.sequence(PRINT, ASSIGNMENT_EXPR)));

    b.rule(NEW_EXPR).is(NEW, CLASS_NAME_REFERENCE, b.optional(FUNCTION_CALL_PARAMETER_LIST)); // TODO: TEST
    b.rule(CLASS_NAME_REFERENCE).is(b.firstOf(
      b.sequence(BASE_VARIABLE, b.zeroOrMore(ARROW, OBJECT_PROPERTY)),
      CLASS_NAME));

    // Unary expression
    b.rule(UNARY_EXPR).is(b.firstOf(  // TODO: re-arrange & complete
      b.sequence(b.firstOf(INC, DEC), POSTFIX_EXPR),
      b.sequence(b.firstOf(PLUS, MINUS, TILDA, BANG), UNARY_EXPR),
      b.sequence(PHPPunctuator.AT, POSTFIX_EXPR),
      b.sequence(CAST_TYPE, ASSIGNMENT_EXPR), // TODO: check assignment_expr
      POSTFIX_EXPR));

    // Binary expressions
    b.rule(CONCATENATION_EXPR).is(UNARY_EXPR, b.zeroOrMore(DOT, UNARY_EXPR));
    b.rule(MULTIPLICATIVE_EXPR).is(CONCATENATION_EXPR, b.zeroOrMore(MULIPLICATIVE_OPERATOR, CONCATENATION_EXPR));
    b.rule(ADDITIVE_EXPR).is(MULTIPLICATIVE_EXPR, b.zeroOrMore(ADDITIVE_OPERATOR, MULTIPLICATIVE_EXPR));
    b.rule(SHIFT_EXPR).is(ADDITIVE_EXPR, b.zeroOrMore(SHIFT_OPERATOR, ADDITIVE_EXPR));
    b.rule(RELATIONAL_EXPR).is(SHIFT_EXPR, b.zeroOrMore(RELATIONAL_OPERATOR, SHIFT_EXPR));
    b.rule(EQUALITY_EXPR).is(RELATIONAL_EXPR, b.zeroOrMore(EQUALITY_OPERATOR, RELATIONAL_EXPR));
    b.rule(BITEWISE_AND_EXPR).is(EQUALITY_EXPR, b.zeroOrMore(AND, EQUALITY_EXPR));
    b.rule(BITEWISE_XOR_EXPR).is(BITEWISE_AND_EXPR, b.zeroOrMore(XOR, BITEWISE_AND_EXPR));
    b.rule(BITEWISE_OR_EXPR).is(BITEWISE_XOR_EXPR, b.zeroOrMore(OR, BITEWISE_XOR_EXPR));
    b.rule(LOGICAL_AND_EXPR).is(BITEWISE_OR_EXPR, b.zeroOrMore(LOGICAL_AND_OPERATOR, BITEWISE_OR_EXPR));
    b.rule(LOGICAL_XOR_EXPR).is(LOGICAL_AND_EXPR, b.zeroOrMore(PHPKeyword.XOR, LOGICAL_AND_EXPR));
    b.rule(LOGICAL_OR_EXPR).is(LOGICAL_XOR_EXPR, b.zeroOrMore(LOGICAL_OR_OPERATOR, LOGICAL_XOR_EXPR));
    b.rule(CONDITIONAL_EXPR).is(LOGICAL_OR_EXPR, b.optional(QUERY, b.optional(ASSIGNMENT_EXPR), COLON, LOGICAL_OR_EXPR));

    b.rule(ASSIGNMENT_EXPR).is(b.firstOf(
      b.sequence(VARIABLE, EQU, AND, b.firstOf(VARIABLE, NEW_EXPR)), //TODO: check 'AND' or rename
      b.sequence(CONDITIONAL_EXPR, ASSIGNMENT_OPERATOR, ASSIGNMENT_EXPR),
      CONDITIONAL_EXPR));
    b.rule(ASSIGNMENT_OPERATOR).is(b.firstOf(EQU, COMPOUND_ASSIGNMENT, LOGICAL_ASSIGNMENT));
    b.rule(COMPOUND_ASSIGNMENT).is(b.firstOf(STAR_EQU, DIVEQUAL, MOD_EQU, PLUS_EQU, MINUS_EQU, SL_EQU, SR_EQU, CONCATEQUAL));
    b.rule(LOGICAL_ASSIGNMENT).is(b.firstOf(ANDEQUAL, XOR_EQU, OR_EQU));

    b.rule(LEXICAL_VARS).is(USE, LPARENTHESIS, LEXICAL_VAR_LIST, RPARENTHESIS);  // Can be empty
    b.rule(LEXICAL_VAR_LIST).is(LEXICAL_VAR, b.zeroOrMore(COMMA, LEXICAL_VAR));
    b.rule(LEXICAL_VAR).is(b.optional(AND), VAR_IDENTIFIER);
  }

  public static void declaration(LexerfulGrammarBuilder b) {
    b.rule(USE_CONST_DECLARATION_STATEMENT).is(USE, CONST, USE_FUNCTION_DECLARATIONS, SEMICOLON);
    b.rule(USE_FUNCTION_DECLARATION_STATEMENT).is(USE, FUNCTION, USE_FUNCTION_DECLARATIONS, SEMICOLON); // TODO: to check
    b.rule(USE_FUNCTION_DECLARATIONS).is(USE_FUNCTION_DECLARATION, b.zeroOrMore(COMMA, USE_FUNCTION_DECLARATION));
    b.rule(USE_FUNCTION_DECLARATION).is(NAMESPACE_NAME, b.optional(AS, IDENTIFIER));

    b.rule(USE_DECLARATIONS).is(USE_DECLARATION, b.zeroOrMore(COMMA, USE_DECLARATION));
    b.rule(USE_DECLARATION).is(b.firstOf(
      b.sequence(NAMESPACE_NAME, AS, IDENTIFIER),
      NAMESPACE_NAME));

    // Class declaration
    b.rule(CLASS_DECLARATION).is(CLASS_ENTRY_TYPE, IDENTIFIER, b.optional(EXTENDS_FROM), b.optional(IMPLEMENTS_LIST),
      LCURLYBRACE, b.zeroOrMore(CLASS_STATEMENT), RCURLYBRACE);
    b.rule(CLASS_ENTRY_TYPE).is(b.firstOf(
      b.sequence(b.optional(CLASS_TYPE), CLASS),
      TRAIT));
    b.rule(CLASS_TYPE).is(b.firstOf(ABSTRACT, FINAL));

    b.rule(EXTENDS_FROM).is(EXTENDS, FULLY_QUALIFIED_CLASS_NAME);
    b.rule(IMPLEMENTS_LIST).is(IMPLEMENTS, INTERFACE_LIST);
    b.rule(INTERFACE_LIST).is(FULLY_QUALIFIED_CLASS_NAME, b.zeroOrMore(COMMA, FULLY_QUALIFIED_CLASS_NAME));
    b.rule(FULLY_QUALIFIED_CLASS_NAME).is(b.firstOf(
      b.sequence(NAMESPACE, FULLY_QUALIFIED_NAME),
      NAMESPACE_NAME));

    // Interface declaration
    b.rule(INTERFACE_DECLARATION).is(INTERFACE, IDENTIFIER, b.optional(INTERFACE_EXTENDS_LIST),
      LCURLYBRACE, b.zeroOrMore(CLASS_STATEMENT), RCURLYBRACE);
    b.rule(INTERFACE_EXTENDS_LIST).is(EXTENDS, INTERFACE_LIST);

    b.rule(CONSTANT_DECLARATION).is(CONST, CONSTANT_VAR_LIST, SEMICOLON);
    b.rule(CONSTANT_VAR_LIST).is(CONSTANT_VAR, b.zeroOrMore(COMMA, CONSTANT_VAR));
    b.rule(CONSTANT_VAR).is(IDENTIFIER, EQU, STATIC_SCALAR);

    b.rule(METHOD_DECLARATION).is(b.zeroOrMore(MEMBER_MODIFIER), FUNCTION, b.optional(REFERENCE), IDENTIFIER, // TODO: TEST !
      LPARENTHESIS, b.optional(PARAMETER_LIST), RPARENTHESIS, METHOD_BODY);
    b.rule(METHOD_BODY).is(b.firstOf(SEMICOLON, BLOCK));

    b.rule(PARAMETER_LIST).is(PARAMETER, b.zeroOrMore(COMMA, PARAMETER));
    b.rule(PARAMETER).is(b.optional(OPTIONAL_CLASS_TYPE), b.optional(AND), b.optional(ELIPSIS), VAR_IDENTIFIER, b.optional(EQU, STATIC_SCALAR));
    b.rule(OPTIONAL_CLASS_TYPE).is(b.firstOf(ARRAY, CALLABLE, FULLY_QUALIFIED_CLASS_NAME));

    b.rule(CLASS_VARIABLE_DECLARATION).is(VARIABLE_MODIFIERS, VARIABLE_DECLARATION, b.zeroOrMore(COMMA, VARIABLE_DECLARATION), SEMICOLON);
    b.rule(VARIABLE_DECLARATION).is(VAR_IDENTIFIER, b.optional(EQU, STATIC_SCALAR));
    b.rule(VARIABLE_MODIFIERS).is(b.firstOf(VAR, b.oneOrMore(MEMBER_MODIFIER)));

    b.rule(CLASS_CONSTANT_DECLARATION).is(CONST, MEMBER_CONST_DECLARATION, b.zeroOrMore(COMMA, MEMBER_CONST_DECLARATION), SEMICOLON);
    b.rule(MEMBER_CONST_DECLARATION).is(IDENTIFIER, b.optional(EQU, STATIC_SCALAR));

    b.rule(MEMBER_MODIFIER).is(b.firstOf(
      PUBLIC,
      PROTECTED,
      PRIVATE,
      STATIC,
      ABSTRACT,
      FINAL));
  }

  public static void statement(LexerfulGrammarBuilder b) {
    b.rule(TOP_STATEMENT_LIST).is(b.zeroOrMore(TOP_STATEMENT));

    b.rule(TOP_STATEMENT).is(b.firstOf(
      CLASS_DECLARATION,
      FUNCTION_DECLARATION,
      INTERFACE_DECLARATION,
      NAMESPACE_STATEMENT,
      USE_STATEMENT,
      USE_FUNCTION_DECLARATION_STATEMENT,
      CONSTANT_DECLARATION,
      HALT_COMPILER_STATMENT,
      STATEMENT
    ));

    b.rule(NAMESPACE_NAME).is(b.firstOf(
      FULLY_QUALIFIED_NAME,
      QUALIFIED_NAME,
      UNQUALIFIED_NAME));

    b.rule(UNQUALIFIED_NAME).is(IDENTIFIER);
    b.rule(QUALIFIED_NAME).is(IDENTIFIER, b.oneOrMore(NS_SEPARATOR, IDENTIFIER));
    b.rule(FULLY_QUALIFIED_NAME).is(NS_SEPARATOR, IDENTIFIER, b.zeroOrMore(NS_SEPARATOR, IDENTIFIER));

    b.rule(NAMESPACE_STATEMENT).is(b.firstOf(
      b.sequence(NAMESPACE, NAMESPACE_NAME, SEMICOLON),
      b.sequence(NAMESPACE, b.optional(NAMESPACE_NAME), LCURLYBRACE, b.optional(TOP_STATEMENT_LIST), RCURLYBRACE)));

    b.rule(USE_STATEMENT).is(USE, USE_DECLARATIONS, SEMICOLON);

    b.rule(HALT_COMPILER_STATMENT).is(HALT_COMPILER, LPARENTHESIS, RPARENTHESIS, SEMICOLON);

    b.rule(REFERENCE).is(AND);
    b.rule(FUNCTION_DECLARATION).is(FUNCTION, b.optional(REFERENCE), IDENTIFIER,
      LPARENTHESIS, b.optional(PARAMETER_LIST), RPARENTHESIS, BLOCK);

    b.rule(STATEMENT).is(b.firstOf(
      BLOCK,
      LABEL,
      ALTERNATIVE_IF_STATEMENT,
      THROW_STATEMENT,
      IF_STATEMENT,
      WHILE_STATEMENT,
      DO_WHILE_STATEMENT,
      FOREACH_STATEMENT,
      FOR_STATEMENT,
      SWITCH_STATEMENT,
      BREAK_STATEMENT,
      CONTINUE_STATEMENT,
      RETURN_STATEMENT,
      EMPTY_STATEMENT,
      YIELD_STATEMENT,
      GLOBAL_STATEMENT,
      STATIC_STATEMENT,
      ECHO_STATEMENT,
      TRY_STATEMENT,
      // TODO: INLINE_HTML ?
      UNSET_VARIABLE_STATEMENT,
      EXPRESSION_STATEMENT
    ));

    b.rule(EMPTY_STATEMENT).is(SEMICOLON);

    b.rule(LABEL).is(IDENTIFIER, COLON);
    b.rule(BLOCK).is(LCURLYBRACE, b.optional(INNER_STATEMENT_LIST), RCURLYBRACE);

    b.rule(IF_STATEMENT).is(IF, PARENTHESIS_EXPRESSION, STATEMENT, b.optional(ELSIF_LIST), b.optional(ELSE_CLAUSE));
    b.rule(ELSIF_LIST).is(b.oneOrMore(ELSEIF, PARENTHESIS_EXPRESSION, STATEMENT));
    b.rule(ELSE_CLAUSE).is(ELSE, STATEMENT);

    b.rule(ALTERNATIVE_IF_STATEMENT).is(IF, PARENTHESIS_EXPRESSION, COLON, b.optional(INNER_STATEMENT_LIST),
      b.optional(ALTERNATIVE_ELSIF_LIST), b.optional(ALTERNATIVE_ELSE_CLAUSE), ENDIF, SEMICOLON);
    b.rule(ALTERNATIVE_ELSIF_LIST).is(b.oneOrMore(ELSEIF, PARENTHESIS_EXPRESSION, COLON, b.optional(INNER_STATEMENT_LIST)));
    b.rule(ALTERNATIVE_ELSE_CLAUSE).is(ELSE, COLON, b.optional(INNER_STATEMENT_LIST));

    b.rule(WHILE_STATEMENT).is(WHILE, PARENTHESIS_EXPRESSION, INNER_WHILE_STATEMENT);
    b.rule(INNER_WHILE_STATEMENT).is(b.firstOf(
      b.sequence(COLON, b.optional(INNER_STATEMENT_LIST), ENDWHILE, SEMICOLON),
      STATEMENT));

    b.rule(DO_WHILE_STATEMENT).is(DO, STATEMENT, WHILE, PARENTHESIS_EXPRESSION, SEMICOLON);

    b.rule(FOR_STATEMENT).is(FOR, LPARENTHESIS, b.optional(FOR_EXRR), SEMICOLON, b.optional(FOR_EXRR), SEMICOLON, b.optional(FOR_EXRR),
      RPARENTHESIS, b.optional(INNER_FOR_STATEMENT));
    b.rule(FOR_EXRR).is(ASSIGNMENT_EXPR, b.zeroOrMore(COMMA, ASSIGNMENT_EXPR));
    b.rule(INNER_FOR_STATEMENT).is(b.firstOf(
      b.sequence(COLON, b.optional(INNER_STATEMENT_LIST), ENDFOR, SEMICOLON),
      STATEMENT));

    b.rule(SWITCH_STATEMENT).is(SWITCH, PARENTHESIS_EXPRESSION, SWITCH_CASE_LIST);
    b.rule(SWITCH_CASE_LIST).is(b.firstOf(
      b.sequence(LCURLYBRACE, b.optional(SEMICOLON), b.optional(CASE_LIST), RCURLYBRACE),
      b.sequence(COLON, b.optional(SEMICOLON), b.optional(CASE_LIST), ENDSWITCH, SEMICOLON)));

    b.rule(CASE_LIST).is(b.oneOrMore(b.firstOf(CASE_CLAUSE, DEFAULT_CLAUSE)));
    b.rule(CASE_CLAUSE).is(CASE, ASSIGNMENT_EXPR, CASE_SEPARTOR, b.optional(INNER_STATEMENT_LIST));
    b.rule(DEFAULT_CLAUSE).is(DEFAULT, CASE_SEPARTOR, b.optional(INNER_STATEMENT_LIST));
    b.rule(CASE_SEPARTOR).is(b.firstOf(COLON, SEMICOLON));

    b.rule(BREAK_STATEMENT).is(BREAK, b.optional(ASSIGNMENT_EXPR), SEMICOLON);
    b.rule(CONTINUE_STATEMENT).is(CONTINUE, b.optional(ASSIGNMENT_EXPR), SEMICOLON);

    b.rule(RETURN_STATEMENT).is(RETURN, b.optional(ASSIGNMENT_EXPR), SEMICOLON);
    b.rule(EXPRESSION_STATEMENT).is(ASSIGNMENT_EXPR, SEMICOLON);

    b.rule(FOREACH_STATEMENT).is(FOREACH, LPARENTHESIS, FOREACH_EXPR, RPARENTHESIS, b.optional(INNER_FOREACH_STATEMENT));
    b.rule(FOREACH_EXPR).is(ASSIGNMENT_EXPR, AS, FOREACH_VARIABLE, b.optional(DOUBLEARROW, FOREACH_VARIABLE));
    b.rule(FOREACH_VARIABLE).is(b.firstOf(
      b.sequence(b.optional(AND), VARIABLE),
      b.sequence(LIST, LPARENTHESIS, ASSIGNMENT_LIST, RPARENTHESIS)));
    b.rule(INNER_FOREACH_STATEMENT).is(b.firstOf(
      b.sequence(COLON, b.optional(INNER_STATEMENT_LIST), ENDFOREACH, SEMICOLON),
      STATEMENT));

    b.rule(DECLARE_STATEMENT).is(DECLARE, LPARENTHESIS, DECLARE_LIST, RPARENTHESIS, INNER_DECLARE_STATEMENT);
    b.rule(DECLARE_LIST).is(MEMBER_CONST_DECLARATION, b.zeroOrMore(COMMA, MEMBER_CONST_DECLARATION));
    b.rule(INNER_DECLARE_STATEMENT).is(b.firstOf(
      b.sequence(COLON, b.optional(INNER_STATEMENT_LIST), ENDDECLARE, SEMICOLON),
      STATEMENT));

    b.rule(TRY_STATEMENT).is(TRY, LCURLYBRACE, b.optional(INNER_STATEMENT_LIST), RCURLYBRACE, b.zeroOrMore(CATCH_STATEMENT), b.optional(FINALLY_STATEMENT));
    b.rule(CATCH_STATEMENT).is(CATCH, LPARENTHESIS, FULLY_QUALIFIED_CLASS_NAME, VAR_IDENTIFIER, RPARENTHESIS, LCURLYBRACE, b.optional(INNER_STATEMENT_LIST), RCURLYBRACE);
    b.rule(FINALLY_STATEMENT).is(FINALLY, LCURLYBRACE, b.optional(INNER_STATEMENT_LIST), RCURLYBRACE);

    b.rule(THROW_STATEMENT).is(THROW, ASSIGNMENT_EXPR, SEMICOLON); // TODO: TEST when expr complete
    b.rule(GOTO_STATEMENT).is(GOTO, IDENTIFIER, SEMICOLON);

    b.rule(YIELD_STATEMENT).is(YIELD_EXPRESSION, SEMICOLON);

    b.rule(GLOBAL_STATEMENT).is(GLOBAL, GLOBAL_VAR_LIST, SEMICOLON);
    b.rule(GLOBAL_VAR_LIST).is(GLOBAL_VAR, b.zeroOrMore(COMMA, GLOBAL_VAR));
    b.rule(GLOBAL_VAR).is(b.firstOf(
      b.sequence(DOLAR, ASSIGNMENT_EXPR),
      VAR_IDENTIFIER));

    b.rule(STATIC_STATEMENT).is(STATIC, STATIC_VAR_LIST, SEMICOLON);
    b.rule(STATIC_VAR_LIST).is(STATIC_VAR, b.zeroOrMore(COMMA, STATIC_VAR));
    b.rule(STATIC_VAR).is(VAR_IDENTIFIER, b.optional(EQU, STATIC_SCALAR));

    b.rule(ECHO_STATEMENT).is(ECHO, ASSIGNMENT_EXPR, b.zeroOrMore(COMMA, ASSIGNMENT_EXPR), SEMICOLON); // TODO: TEST when expr complete

    b.rule(UNSET_VARIABLE_STATEMENT).is(UNSET, LPARENTHESIS, UNSET_VARIABLES, RPARENTHESIS, SEMICOLON);
    b.rule(UNSET_VARIABLES).is(VARIABLE, b.zeroOrMore(COMMA, VARIABLE));

    b.rule(CLASS_STATEMENT).is(b.firstOf(
      METHOD_DECLARATION,
      CLASS_VARIABLE_DECLARATION,
      CLASS_CONSTANT_DECLARATION,
      TRAIT_USE_STATEMENT));

    b.rule(TRAIT_USE_STATEMENT).is(USE, INTERFACE_LIST, TRAIT_ADAPTATIONS);
    b.rule(TRAIT_ADAPTATIONS).is(b.firstOf(
      b.sequence(LCURLYBRACE, b.zeroOrMore(TRAIT_ADAPTATION_STATEMENT), RCURLYBRACE),
      SEMICOLON));
    b.rule(TRAIT_ADAPTATION_STATEMENT).is(b.firstOf(TRAIT_PRECEDENCE, TRAIT_ALIAS), SEMICOLON);
    b.rule(TRAIT_PRECEDENCE).is(TRAIT_METHOD_REFERENCE_FULLY_QUALIFIED, INSTEADOF, INTERFACE_LIST);
    b.rule(TRAIT_ALIAS).is(TRAIT_METHOD_REFERENCE, AS, b.firstOf(
      b.sequence(b.optional(MEMBER_MODIFIER), IDENTIFIER),
      MEMBER_MODIFIER));
    b.rule(TRAIT_METHOD_REFERENCE).is(b.firstOf(TRAIT_METHOD_REFERENCE_FULLY_QUALIFIED, IDENTIFIER));
    b.rule(TRAIT_METHOD_REFERENCE_FULLY_QUALIFIED).is(FULLY_QUALIFIED_CLASS_NAME, DOUBLECOLON, IDENTIFIER);

    b.rule(INNER_STATEMENT_LIST).is(b.oneOrMore(b.firstOf(
      FUNCTION_DECLARATION,
      CLASS_DECLARATION,
      INTERFACE_DECLARATION,
      HALT_COMPILER_STATMENT,
      STATEMENT)));
  }

}
