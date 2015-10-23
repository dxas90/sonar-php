/*
 * SonarQube PHP Plugin
 * Copyright (C) 2010 SonarSource and Akram Ben Aissi
 * sonarqube@googlegroups.com
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
package org.sonar.php.tree.impl.expression;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;
import org.sonar.php.PHPTreeModelTest;
import org.sonar.plugins.php.api.tree.Tree.Kind;
import org.sonar.plugins.php.api.tree.expression.CompoundVariableTree;

public class CompoundVariableTreeTest extends PHPTreeModelTest {

  @Test
  public void test() throws Exception {
    CompoundVariableTree tree = parse("${$a}", Kind.COMPOUND_VARIABLE_NAME);

    assertThat(tree.is(Kind.COMPOUND_VARIABLE_NAME)).isTrue();
    assertThat(tree.openDollarCurlyBraceToken().text()).isEqualTo("${");
    assertThat(expressionToString(tree.variableExpression())).isEqualTo("$a");
    assertThat(tree.variableExpression().is(Kind.VARIABLE_IDENTIFIER)).isTrue();
    assertThat(tree.closeCurlyBraceToken().text()).isEqualTo("}");
  }

}
