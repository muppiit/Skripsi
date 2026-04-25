/* eslint-disable react/prop-types */
/* eslint-disable no-unused-vars */
import React, { useState, useEffect } from "react";
import { Form, Input, Select, Modal, message } from "antd";
import { getDepartments } from "@/api/department";

const { TextArea } = Input;
const { Option } = Select;

const AddStudyProgramForm = ({ visible, onCancel, onOk, confirmLoading }) => {
  const [form] = Form.useForm();
  const [departmentList, setDepartmentList] = useState([]);
  const [loadingDepts, setLoadingDepts] = useState(false);

  const fetchDepartments = async () => {
    setLoadingDepts(true);
    try {
      const result = await getDepartments();
      if (result.data.statusCode === 200) {
        setDepartmentList(result.data.content || []);
      } else {
        message.error("Gagal mengambil data jurusan");
      }
    } catch (error) {
      message.error("Terjadi kesalahan: " + error.message);
    } finally {
      setLoadingDepts(false);
    }
  };

  useEffect(() => {
    if (visible) {
      fetchDepartments();
      form.resetFields();
    }
  }, [visible, form]);

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      onOk(values);
    } catch (info) {
      console.log("Validate Failed:", info);
    }
  };

  const formItemLayout = {
    labelCol: { xs: { span: 24 }, sm: { span: 8 } },
    wrapperCol: { xs: { span: 24 }, sm: { span: 16 } },
  };

  return (
    <Modal
      title="Tambah Program Studi"
      open={visible}
      onCancel={() => {
        form.resetFields();
        onCancel();
      }}
      onOk={handleSubmit}
      confirmLoading={confirmLoading}
      okText="Simpan"
      cancelText="Batal"
    >
      <Form form={form} {...formItemLayout}>
        <Form.Item
          label="Jurusan:"
          name="department_id"
          rules={[{ required: true, message: "Silahkan pilih jurusan" }]}
        >
          <Select
            placeholder="Pilih Jurusan"
            loading={loadingDepts}
            showSearch
            optionFilterProp="children"
          >
            {departmentList.map((dept) => (
              <Option key={dept.id} value={dept.id}>
                {dept.name}
              </Option>
            ))}
          </Select>
        </Form.Item>

        <Form.Item
          label="Nama Prodi:"
          name="name"
          rules={[{ required: true, message: "Silahkan isi nama program studi" }]}
        >
          <Input placeholder="Nama program studi" />
        </Form.Item>

        <Form.Item label="Deskripsi Prodi:" name="description">
          <TextArea rows={4} placeholder="Silahkan isi deskripsi program studi" />
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default AddStudyProgramForm;
