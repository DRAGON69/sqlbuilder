//
/*
Copyright (c) 2008 Health Market Science, Inc.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
USA

You can contact Health Market Science at info@healthmarketscience.com
or at the following address:

Health Market Science
2700 Horizon Drive
Suite 200
King of Prussia, PA 19406
*/
//
package com.healthmarketscience.sqlbuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import com.healthmarketscience.common.util.AppendableExt;
import com.healthmarketscience.sqlbuilder.dbspec.Column;
import com.healthmarketscience.sqlbuilder.dbspec.Table;

/**
 * Outputs an "IN" condition
 * <code>"(&lt;column&gt; IN (&lt;rightObj1&gt;, &lt;rightObj2&gt;, ...) )"</code>
 * 
 * @author Eric Bernstein
 */
public class InCondition extends Condition {

  private boolean _negate;
  private SqlObject _leftValue;
  private SqlObjectList<SqlObject> _rightValues = SqlObjectList.create();

  /**
   * Column {@code Object} -&gt; {@code SqlObject} conversions handled by
   * {@link Converter#toColumnSqlObject}.
   * Value {@code Object} -&gt; {@code SqlObject} conversions handled by
   * {@link Converter#COLUMN_VALUE_TO_OBJ}.
   */
  public InCondition(Object leftObj, Object... rightObjs) {
    this(leftObj,
         (rightObjs != null ?
          Arrays.asList(rightObjs) :
          Collections.emptyList())); 
  }

  /**
   * Column {@code Object} -&gt; {@code SqlObject} conversions handled by
   * {@link Converter#toColumnSqlObject}.
   * Value {@code Object} -&gt; {@code SqlObject} conversions handled by
   * {@link Converter#COLUMN_VALUE_TO_OBJ}.
   */
  public InCondition(Object leftObj, Collection<?> rightObjs) {
    _leftValue = Converter.toColumnSqlObject(leftObj);
    _rightValues.addObjects(Converter.COLUMN_VALUE_TO_OBJ, rightObjs);
  }

  /**
   * Adds the given object to the tested value.
   * <p>
   * Value {@code Object} -&gt; {@code SqlObject} conversions handled by
   * {@link Converter#COLUMN_VALUE_TO_OBJ}.
   */
  public InCondition addObject(Object obj) {
    return addObjects(obj);
  }
  
  /**
   * Adds the given objects to the tested values.
   * <p>
   * Value {@code Object} -&gt; {@code SqlObject} conversions handled by
   * {@link Converter#COLUMN_VALUE_TO_OBJ}.
   */
  public InCondition addObjects(Object... objs) {
    _rightValues.addObjects(Converter.COLUMN_VALUE_TO_OBJ, objs);
    return this;
  }
  
  /** Sets whether or not the in condition should be negated or not */
  public InCondition setNegate(boolean negate) {
    _negate = negate;
    return this;
  }

  @Override
  public boolean isEmpty() {
    // if the condition is negated and the list is empty, this is essentially
    // and "empty" condition, so don't bother outputting anything
    return(_negate && _rightValues.isEmpty());
  }
  
  @Override
  protected void collectSchemaObjects(Collection<Table> tables,
      Collection<Column> columns) {
    _leftValue.collectSchemaObjects(tables, columns);
    _rightValues.collectSchemaObjects(tables, columns);
  }

  @Override
  public void appendTo(AppendableExt app) throws IOException {
    if(!isEmpty()) {
      // ( x in (y1,y2,y3) )
      app.append("(").append(_leftValue)
        .append(_negate ? " NOT IN (" : " IN (")
        .append(_rightValues).append(") )");
    }
  }
}