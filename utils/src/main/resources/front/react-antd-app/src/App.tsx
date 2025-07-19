import { Button, Table } from 'antd';
import { useEffect, useState } from 'react';
import axios from 'axios';

const columns = [
  { title: 'ID', dataIndex: 'id' },
  { title: '姓名', dataIndex: 'name' },
  { title: '年龄', dataIndex: 'age' }
];

function App() {
  const [data, setData] = useState([]);

  useEffect(() => {
    axios.get('/api/users').then(res => setData(res.data));
  }, []);

  return (
    <div style={{ padding: 24 }}>
      <h2>用户列表</h2>
      <Button type="primary" style={{ marginBottom: 16 }}>添加用户</Button>
      <Table rowKey="id" columns={columns} dataSource={data} />
    </div>
  );
}

export default App;
