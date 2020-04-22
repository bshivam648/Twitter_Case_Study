import unittest
from twitter import app


class ErrorTests(unittest.TestCase):
	# initialization logic for the test suite declared in the test module
	# code that is executed before all tests in one test run
	@classmethod
	def setUpClass(cls):
		pass

	# clean up logic for the test suite declared in the test module
	# code that is executed after all tests in one test run
	@classmethod
	def tearDownClass(cls):
		pass

	# initialization logic
	# code that is executed before each test
	def setUp(self):
		self.app = app.test_client()
		self.app.testing = True

	# clean up logic
	# code that is executed after each test
	def tearDown(self):
		pass

	# test method
	def test_pagenotfound_statuscode(self):
		result = self.app.get("/invalid/url")

		self.assertEqual(result.status_code, 404)

	# test method for query count
	def test_pagenotfound_statuscodecount(self):
		result = self.app.get("/api/count")

		self.assertNotEqual(result.status_code, 404)

	# test method for query1
	def test_pagenotfound_statuscode1(self):
		result = self.app.get("/api/query1")

		self.assertNotEqual(result.status_code, 404)

	# test method for query2
	def test_pagenotfound_statuscode2(self):
		result = self.app.get("/api/query2")

		self.assertNotEqual(result.status_code, 404)

	# test method for query3
	def test_pagenotfound_statuscode3(self):
		result = self.app.get("/api/query3")

		self.assertNotEqual(result.status_code, 404)

	# test method for query4
	def test_pagenotfound_statuscode4( self ):
		result = self.app.get("/api/query4")
		self.assertNotEqual(result.status_code, 404)

	# test method for query5
	def test_pagenotfound_statuscode5( self ):
		result = self.app.get("/api/query5")
		self.assertNotEqual(result.status_code, 404)

	# test method for query6
	def test_pagenotfound_statuscode6( self ):
		result = self.app.get("/api/query6")
		self.assertNotEqual(result.status_code, 404)

	# test method for query7
	def test_pagenotfound_statuscode7( self ):
		result = self.app.get("/api/query7")
		self.assertNotEqual(result.status_code, 404)

