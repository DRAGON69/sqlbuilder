/*
Copyright (c) 2008 Health Market Science, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.healthmarketscience.sqlbuilder;

import java.io.IOException;
import com.healthmarketscience.common.util.AppendableExt;
import com.healthmarketscience.sqlbuilder.dbspec.Column;
import com.healthmarketscience.sqlbuilder.dbspec.Function;


/**
 * Outputs a function call
 * <code>"&lt;name&gt;([&lt;param1&gt;, ... &lt;paramN&gt;])"</code>.
 *
 * @author James Ahlborn
 */
public class FunctionCall extends Expression
{
  private boolean _isDistinct;
  private SqlObject _functionName;
  private SqlObjectList<SqlObject> _params = SqlObjectList.create();

  public FunctionCall(Function function) {
    this((Object)function);
  }

  /**
   * {@code Object} -&gt; {@code SqlObject} conversions handled by
   * {@link Converter#toCustomFunctionSqlObject(Object)}.
   */
  public FunctionCall(Object functionNameStr) {
    _functionName = Converter.toCustomFunctionSqlObject(functionNameStr);
  }

  @Override
  public boolean hasParens() { return false; }

  /** Iff isDistinct is <code>true</code>, adds the DISTINCT keyword to the
      parameter clause */
  public FunctionCall setIsDistinct(boolean isDistinct) {
    _isDistinct = isDistinct;
    return this;
  }
    
  /**
   * Adds custom parameters to the function call.
   * <p>
   * {@code Object} -&gt; {@code SqlObject} conversions handled by
   * {@link Converter#COLUMN_VALUE_TO_OBJ}.
   */
  public FunctionCall addCustomParams(Object... params) {
    _params.addObjects(Converter.COLUMN_VALUE_TO_OBJ, params);
    return this;
  }
    
  /** Adds column parameters to the function call as
      <code>"&lt;alias&gt;.&lt;column&gt;"</code>. */
  public FunctionCall addColumnParams(Column... columns) {
    return addCustomParams((Object[])columns);
  }
    
  /**
   * Adds a numeric value parameter to the function call.
   * <p>
   * {@code Object} -&gt; {@code SqlObject} conversions handled by
   * {@link Converter#COLUMN_VALUE_TO_OBJ}.
   */
  public FunctionCall addNumericValueParam(Object obj) {
    return addCustomParams(obj);
  }
    
  @Override
  protected void collectSchemaObjects(ValidationContext vContext) {
    _functionName.collectSchemaObjects(vContext);
    _params.collectSchemaObjects(vContext);
  }
    
  @Override
  public void appendTo(AppendableExt app) throws IOException {
    app.append(_functionName).append("(");
    if(_isDistinct) {
      app.append("DISTINCT ");
    }
    app.append(_params).append(")");
  }

  /**
   * Convenience method for generating a FunctionCall using the standard AVG
   * function.
   */
  public static FunctionCall avg() {
    return new FunctionCall(new CustomSql("AVG"));
  }
  
  /**
   * Convenience method for generating a FunctionCall using the standard MIN
   * function.
   */
  public static FunctionCall min() {
    return new FunctionCall(new CustomSql("MIN"));
  }
  
  /**
   * Convenience method for generating a FunctionCall using the standard MAX
   * function.
   */
  public static FunctionCall max() {
    return new FunctionCall(new CustomSql("MAX"));
  }
  
  /**
   * Convenience method for generating a FunctionCall using the standard SUM
   * function.
   */
  public static FunctionCall sum() {
    return new FunctionCall(new CustomSql("SUM"));
  }
  
  /**
   * Convenience method for generating a FunctionCall using the standard COUNT
   * function.
   */
  public static FunctionCall count() {
    return new FunctionCall(new CustomSql("COUNT"));
  }
  
  /**
   * Convenience method for generating a FunctionCall using the standard COUNT
   * function with the single parameter '*'.
   */
  public static FunctionCall countAll() {
    return (new FunctionCall(new CustomSql("COUNT")))
      .addCustomParams(ALL_SYMBOL);
  }
  
}
