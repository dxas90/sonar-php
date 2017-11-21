/*
 * SonarQube PHP Plugin
 * Copyright (C) 2010-2017 SonarSource SA
 * mailto:info AT sonarsource DOT com
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
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.php.tree.impl.expression;

import org.junit.Test;
import org.sonar.php.PHPTreeModelTest;
import org.sonar.php.parser.PHPLexicalGrammar;
import org.sonar.plugins.php.api.tree.Tree.Kind;
import org.sonar.plugins.php.api.tree.expression.FunctionCallTree;

import static org.assertj.core.api.Assertions.assertThat;

public class FunctionCallTreeTest extends PHPTreeModelTest {

  @Test
  public void without_argument() throws Exception {
    FunctionCallTree tree = parse("f()", PHPLexicalGrammar.FUNCTION_CALL);

    assertThat(tree.is(Kind.FUNCTION_CALL)).isTrue();
    assertThat(expressionToString(tree.callee())).isEqualTo("f");
    assertThat(tree.openParenthesisToken().text()).isEqualTo("(");
    assertThat(tree.arguments()).isEmpty();
    assertThat(tree.closeParenthesisToken().text()).isEqualTo(")");
  }

  @Test
  public void with_argument() throws Exception {
    FunctionCallTree tree = parse("f($p1, $p2)", PHPLexicalGrammar.FUNCTION_CALL);

    assertThat(tree.is(Kind.FUNCTION_CALL)).isTrue();
    assertThat(expressionToString(tree.callee())).isEqualTo("f");
    assertThat(tree.openParenthesisToken().text()).isEqualTo("(");

    assertThat(tree.arguments()).hasSize(2);
    assertThat(tree.arguments().getSeparators()).hasSize(1);
    assertThat(expressionToString(tree.arguments().get(0))).isEqualTo("$p1");
    assertThat(expressionToString(tree.arguments().get(1))).isEqualTo("$p2");

    assertThat(tree.closeParenthesisToken().text()).isEqualTo(")");
  }

}
