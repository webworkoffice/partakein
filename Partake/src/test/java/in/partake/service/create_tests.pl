#!/usr/bin/perl
use strict;
use warnings;

# create test case file name
# ex.) HogeServiceTestCaseBase -> CassandraHogeServiceTest
sub create_testcase_filename() {
  my ($testcasebase_filename, $target_datastore) = @_;
  my $testcase_filename = ucfirst(lc($target_datastore)).$testcasebase_filename;
  $testcase_filename =~ s/TestCaseBase/Test/;
  return $testcase_filename;
}

sub create_testcase() {
  my ($testcasebase_filename, $target_datastore) = @_;
  my $testcase_filename = &create_testcase_filename($testcasebase_filename, $target_datastore);

  my $testcase = <<"EOT";
// this file was generated by create_tests.pl. don't edit by hand.
package in.partake.service.$target_datastore;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import in.partake.resource.PartakeProperties;
import in.partake.service.$testcasebase_filename;

public class $testcase_filename extends $testcasebase_filename {
    \@BeforeClass
    public static void setUpOnce() {
        PartakeProperties.get().reset("$target_datastore");
        reset();
    }

    \@AfterClass
    public static void tearDownOnce() {
        PartakeProperties.get().reset();
        reset();
    }
// common test cases are written in the super class.
}
EOT

  # TODO exception handling
  open FH, sprintf(">%s/%s.java", $target_datastore, $testcase_filename);
  print FH $testcase;
  close(FH);
}

# TODO exception handling
opendir DIR, "." || die $!;
while (my $dir_name = readdir DIR) {
  if (-d $dir_name && $dir_name !~ /^\./ ) {
    if ($dir_name eq "mock") {
      # skip this folder because its test cases should be written by hand.
    } else {
      # TODO exception handling
      opendir INNER_DIR, "." || die $!;
      while (my $testcasebase_name = readdir INNER_DIR) {
        if (-f $testcasebase_name && $testcasebase_name =~ /.java$/ && $testcasebase_name !~ /^Abstract/ && not $testcasebase_name eq "TestService.java") {
          my $testcasebase_filename = $testcasebase_name;
          $testcasebase_filename =~ s/^(.*)[.].*$/$1/; # remove extension
          &create_testcase($testcasebase_filename, $dir_name);
        }
      }
      closedir INNER_DIR;
    }
  }
}
closedir DIR;