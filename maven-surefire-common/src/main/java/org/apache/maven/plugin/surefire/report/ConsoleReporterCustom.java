package org.apache.maven.plugin.surefire.report;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.plugin.surefire.log.api.ConsoleLogger;
import org.apache.maven.plugin.surefire.log.api.Level;
import org.apache.maven.surefire.api.report.TestSetReportEntry;
import org.apache.maven.surefire.shared.utils.logging.MessageBuilder;

import java.util.List;

import static org.apache.maven.plugin.surefire.log.api.Level.resolveLevel;
import static org.apache.maven.plugin.surefire.report.TestSetStats.concatenateWithTestGroup;
import static org.apache.maven.surefire.shared.utils.StringUtils.isBlank;
import static org.apache.maven.surefire.shared.utils.logging.MessageUtils.buffer;

/**
 * Custom class for console reporters.
 *
 * @author <a href="mailto:fabriciorby@hotmail.com">Fabrício Yamamoto</a>
 */
public class ConsoleReporterCustom extends ConsoleReporter
{
    private static final String TEST_SET_STARTING_PREFIX = "+-- ";

    public ConsoleReporterCustom( ConsoleLogger logger,
                                 boolean usePhrasedClassNameInRunning, boolean usePhrasedClassNameInTestCaseSummary )
    {
        super( logger, usePhrasedClassNameInRunning, usePhrasedClassNameInTestCaseSummary );
    }

    @Override
    public void testSetStarting( TestSetReportEntry report )
    {
        getConsoleLogger()
            .info( "|" );

        MessageBuilder builder = buffer().a( TEST_SET_STARTING_PREFIX );

        String runningTestCase =
            concatenateWithTestGroup( builder, report, !isBlank( report.getReportNameWithGroup() ) );

        getConsoleLogger()
                .info( runningTestCase );
    }

    @Override
    public void testSetCompleted( WrappedReportEntry report, TestSetStats testSetStats, List<String> testResults )
    {
        boolean success = testSetStats.getCompletedCount() > 0;
        boolean failures = testSetStats.getFailures() > 0;
        boolean errors = testSetStats.getErrors() > 0;
        boolean skipped = testSetStats.getSkipped() > 0;
        boolean flakes = testSetStats.getSkipped() > 0;

        Level level = resolveLevel( success, failures, errors, skipped, flakes );

        for ( WrappedReportEntry testResult : testSetStats.getReportEntries() )
        {
            final  MessageBuilder builder = buffer().a( "| " + TEST_SET_STARTING_PREFIX );
            if ( testResult.isErrorOrFailure() )
            {
                println( builder.failure( "[XX] " + testResult.getReportName() )
                            .a( " - " + testResult.elapsedTimeAsString() + "s" )
                            .toString(), Level.SUCCESS );
            }
            else if ( testResult.isSkipped() )
            {
                if ( !isBlank( testResult.getReportName() ) )
                {
                    builder.warning( "[??] " + testResult.getReportName() );
                }
                else
                {
                    builder.warning( "[??] " + testResult.getReportSourceName() );
                }

                if ( !isBlank( testResult.getMessage() ) )
                {
                    builder.warning( " (" + testResult.getMessage() + ")" );
                }

                println( builder
                            .a( " - " + testResult.elapsedTimeAsString() + "s" )
                            .toString(), Level.SUCCESS );
            }
            else if ( testResult.isSucceeded() )
            {
                println( builder.success( "[OK] " + testResult.getReportName() )
                                .a( " - " + testResult.elapsedTimeAsString() + "s" )
                                .toString(), Level.SUCCESS );
            }
        }

//        println( testSetStats.getColoredTestSetSummary( report, usePhrasedClassNameInTestCaseSummary ), level );

    }

}
