import sys
from os import walk
import os.path
import fnmatch
import git

def getRepo(remote_url, dst_path):
	if os.path.exists(dst_path) == False:
		if remote_url is None:
			sys.exit('Local repo does not exist! Please provide a valid remote URL!')
		repo = git.Repo.clone_from(remote_url, dst_path)
	else:
		repo = git.Repo(dst_path)
		origin = repo.remotes.origin
		origin.pull()

	assert repo.__class__ is git.Repo
	return repo

def extractTotalBug(repo, key_words):
	all_log = repo.git.log('--pretty=format:%s')
	total = len(all_log.encode('utf-8').splitlines())

	m_log = repo.git.log(key_words, '-i', '--pretty=format:%s')
	matched = len(m_log.encode('utf-8').splitlines())

	percentage = float(matched)/total*100
	print str(matched)+' out of '+str(total)+' = {0:.2f}%'.format(percentage)
	return percentage

def extractBugPerFile(repo, dst_path, key_words):
	java_files = []
	for root, dirs, files in os.walk(dst_path):
		for file_name in fnmatch.filter(files, '*.java'):
			java_files.append(os.path.join(root, file_name))
	
	for file in java_files:
		log = repo.git.log(key_words, '-i', '--pretty=format:%s', '--', file)
		count = len(log.encode('utf-8').splitlines())
		(head, tail) = os.path.split(file)
		print tail+':'+str(count)+':'+str(os.path.getsize(file))

if __name__ == '__main__':
	if len(sys.argv) < 2:
		sys.exit('ERROR: At least one arguments is needed: local repository path')
	elif(len(sys.argv)) == 2:
		remote_url = None;
	else:
		remote_url = sys.argv[2];

	key_words = ['--grep=^fix', '--grep=\sfix', '--grep=bug']
	if len(sys.argv) > 3:
		for i in range(3, len(sys.argv)):
			key_words.append('--grep='+sys.argv[i])

	dst_path = os.path.abspath(sys.argv[1])
	localRepo = getRepo(remote_url, dst_path)
	# extractTotalBug(localRepo, key_words)
	extractBugPerFile(localRepo, dst_path, key_words)
