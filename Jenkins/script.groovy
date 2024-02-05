//building of .Net projects With MSBuild 
def buildapp(){
		echo 'Build is started'
		bat '''
		dotnet restore
		dotnet msbuild /p:Configuration=Release  /p:DeployOnBuild=true /p:DeployDefaultTarget=WebPublish /p:WebPublishMethod=FileSystem /p:DeleteExistingFiles=true  /p:publishUrl=%SRC%
    	echo 'build is completed'
		'''
}


//deployment through ansible playbook
def ansible_deploy(){

		sh ' ansible-playbook /home/hisdu/depl-jenkins.yml --tags sitestate,recycle,zip,sync -e site_name=$SITE_NAME -e src=$SRC -e dest=$DEST -e dest_excl=$EXCL_DEST -e src_excl_dir=$EXCL_SRC_DIR -e src_excl_file=$EXCL_SRC_FILE'
}

// deployment through jenkins agents
def jenkins_deploy_app(){
		bat ''' 
				echo "Stopping the Project"
				cd C:\\Windows\\System32\\inetsrv
				appcmd.exe stop sites %SITE_NAME%
		
				echo "deployment Started"
				winrar a -r -ep1 %EXCL_DEST% %DEST%%FILENAME%.rar %DEST%*
				robocopy /S /MT:5 %SRC% %DEST%  %EXCL_SRC%
				
				echo "Starting the Project"
				cd C:\\Windows\\System32\\inetsrv
				appcmd.exe start sites %SITE_NAME%
				 '''
		echo "Deployment is Completed"
		//exit /b 0
}
return this
