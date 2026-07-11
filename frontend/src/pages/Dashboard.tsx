export default function Dashboard() {
  return (
    <div className="min-h-screen bg-gray-100">
      <div className="container mx-auto py-8">
        <h1 className="text-4xl font-bold text-gray-800 mb-8">USDC Trading Dashboard</h1>
        
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
          {/* Balance Cards */}
          <div className="bg-white rounded-lg shadow p-6">
            <h2 className="text-gray-600 text-sm font-medium">USDC Balance</h2>
            <p className="text-3xl font-bold text-gray-900 mt-2">0.00 USDC</p>
          </div>
          
          <div className="bg-white rounded-lg shadow p-6">
            <h2 className="text-gray-600 text-sm font-medium">Available Balance</h2>
            <p className="text-3xl font-bold text-gray-900 mt-2">$0.00</p>
          </div>

          <div className="bg-white rounded-lg shadow p-6">
            <h2 className="text-gray-600 text-sm font-medium">Total Transactions</h2>
            <p className="text-3xl font-bold text-gray-900 mt-2">0</p>
          </div>

          <div className="bg-white rounded-lg shadow p-6">
            <h2 className="text-gray-600 text-sm font-medium">24h Volume</h2>
            <p className="text-3xl font-bold text-gray-900 mt-2">$0.00</p>
          </div>
        </div>

        {/* Action Buttons */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
          <button className="bg-blue-500 hover:bg-blue-600 text-white font-bold py-3 px-6 rounded-lg">
            Buy USDC
          </button>
          <button className="bg-green-500 hover:bg-green-600 text-white font-bold py-3 px-6 rounded-lg">
            Sell USDC
          </button>
          <button className="bg-purple-500 hover:bg-purple-600 text-white font-bold py-3 px-6 rounded-lg">
            Transfer
          </button>
        </div>

        {/* Recent Transactions */}
        <div className="bg-white rounded-lg shadow p-6">
          <h2 className="text-2xl font-bold text-gray-800 mb-4">Recent Transactions</h2>
          <p className="text-gray-500">No transactions yet</p>
        </div>
      </div>
    </div>
  )
}
