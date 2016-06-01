/*******************************************************************************
 * Copyright (c) 2015 Christian Pontesegger and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:	Christian Pontesegger - initial API and implementation
 *******************************************************************************/

var files = [
             ["Disclaimer.txt", "This is the disclaimer"], 
             ["HowTo.txt", "TODO: write HowTo Content"], 
             ["License.txt", "This project is licensed under the EPL."]
            ];


print("Creating sample project");

loadModule('/System/Resources');
loadModule('/System/UI');

// create project
projectName = showInputDialog("Please provide the name of the project to create", "EclipseCon 2015 Sample project", "Create Project");
if (projectName != null) {
	createProject(projectName);

	// now create files
	for each (fileDescription in files)
		writeFile("workspace://" + projectName + "/" + fileDescription[0], fileDescription[1]);
}



/********BEGIN SIGNATURE********
MC0CFQCRKrh+YtwYGKLi/+OR+9KmN07zYwIUWU4oPhygYPrQua1u3rdAOqW/h+U=

MIIDNzCCAvWgAwIBAgIELNrJgDALBgcqhkjOOAQDBQAwbTEL
MAkGA1UEBhMCSU4xEDAOBgNVBAgTB0d1amFyYXQxEjAQBgNV
BAcTCUFobWVkYWJhZDEQMA4GA1UEChMHVW5rbm93bjEQMA4G
A1UECxMHVW5rbm93bjEUMBIGA1UEAxMLVmFydW4gUmF2YWww
HhcNMTYwMzA1MTE1NjUwWhcNMTYwNjAzMTE1NjUwWjBtMQsw
CQYDVQQGEwJJTjEQMA4GA1UECBMHR3VqYXJhdDESMBAGA1UE
BxMJQWhtZWRhYmFkMRAwDgYDVQQKEwdVbmtub3duMRAwDgYD
VQQLEwdVbmtub3duMRQwEgYDVQQDEwtWYXJ1biBSYXZhbDCC
AbgwggEsBgcqhkjOOAQBMIIBHwKBgQD9f1OBHXUSKVLfSpwu
7OTn9hG3UjzvRADDHj+AtlEmaUVdQCJR+1k9jVj6v8X1ujD2
y5tVbNeBO4AdNG/yZmC3a5lQpaSfn+gEexAiwk+7qdf+t8Yb
+DtX58aophUPBPuD9tPFHsMCNVQTWhaRMvZ1864rYdcq7/Ii
Axmd0UgBxwIVAJdgUI8VIwvMspK5gqLrhAvwWBz1AoGBAPfh
oIXWmz3ey7yrXDa4V7l5lK+7+jrqgvlXTAs9B4JnUVlXjrrU
WU/mcQcQgYC0SRZxI+hMKBYTt88JMozIpuE8FnqLVHyNKOCj
rh4rs6Z1kW6jfwv6ITVi8ftiegEkO8yk8b6oUZCJqIPf4Vrl
nwaSi2ZegHtVJWQBTDv+z0kqA4GFAAKBgQDqLrJZitkj0fqO
RQ/kdKtwHK4Fq6kXfGedp5umydmCVqrIkuCKuw6X2P5gX4Vv
0kqTEG2iWL7Hv3iUCmtaCeKYLSlIyaloJMYPwgcKxWYYMtXn
njfoOAxHywwXxPAygkR/r9TH1VrUSKjvuGvOxdjSNnezjsVL
VEyIXiO76ZfawKMhMB8wHQYDVR0OBBYEFJw/5/p+5vXMZPXx
ZLBh9YLK/zr4MAsGByqGSM44BAMFAAMvADAsAhRA44+6n9Ya
UTnckDGsbZIv450sVAIUA1otxObPsQaTs1EcOEEqODrNHCY=

********END SIGNATURE********/